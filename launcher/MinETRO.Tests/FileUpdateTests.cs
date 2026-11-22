using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using MinETRO.Core;

internal static class FileUpdateTests
{
    private static string Sha(byte[] bytes) => Convert.ToHexString(SHA256.HashData(bytes)).ToLowerInvariant();
    private static void Assert(bool value) { if (!value) throw new Exception("File update assertion failed"); }
    private static async Task Reject(Func<Task> action)
    { try { await action(); } catch (Exception ex) when (ex is IOException or InvalidDataException or OperationCanceledException) { return; } throw new Exception("Expected file update rejection"); }
    public static async Task Run()
    {
        int passed = 0;
        async Task Test(string name, Func<string, Task> action)
        {
            string root = Path.Combine(Path.GetTempPath(), "minetro-files-test-" + Guid.NewGuid().ToString("N")); Directory.CreateDirectory(root);
            try { await action(root); passed++; Console.WriteLine("PASS files: " + name); }
            finally { Directory.Delete(root, true); }
        }
        await Test("strict paths, seed policy, complete pack and chunks", async _ => {
            using var pack = new Fixture("unused"); var m = pack.Manifest("v1");
            Assert(FileManifest.Parse(JsonSerializer.Serialize(m, FileManifest.Json)).Fingerprint == m.Fingerprint);
            foreach (var p in new[] { "saves/world/level.dat", "options.txt", "config/../saves/a", "ModularWarfare/packv4.json", "ModularWarfare/mod_config.json", "mods/CON.jar" })
                await Reject(() => { (m with { Files = [..m.Files, new(p, "managed", 0, Sha([]), [])] }).Validate(); return Task.CompletedTask; });
            await Reject(() => { (m with { Files = [m.Files[0]] }).Validate(); return Task.CompletedTask; });
            await Reject(() => { (m with { Files = [..m.Files, m.Files[0] with { Path = "MODS/other.jar" }] }).Validate(); return Task.CompletedTask; });
            await Reject(() => { (m with { Files = [m.Files[0] with { Bytes = 900 }, m.Files[1]] }).Validate(); return Task.CompletedTask; });
            await Reject(() => { (m with { Files = [..m.Files, new("mods/folder", "managed", 0, Sha([]), []), new("mods/folder/a.jar", "managed", 0, Sha([]), [])] }).Validate(); return Task.CompletedTask; });
        });
        await Test("first install verifies each file and downloads unique chunks", async root => {
            using var pack = new Fixture(root); var m = pack.Manifest("v1"); await pack.Install(m); await pack.Service.Verify(m, default);
            Assert(pack.Network.Bytes == m.Files.Sum(f => f.Bytes)); Assert(File.Exists(pack.Game("mods/metrocraft-port-test.jar")));
        });
        await Test("unchanged files need zero network even after cache removal", async root => {
            using var pack = new Fixture(root); var m = pack.Manifest("v1"); await pack.Install(m); Directory.Delete(Path.Combine(root, "file-cache"), true);
            pack.Network.Bytes = 0; await pack.Install(m with { Version = "v2" }); Assert(pack.Network.Bytes == 0);
        });
        await Test("one changed file downloads only its new chunks; personal files survive", async root => {
            using var pack = new Fixture(root); var m = pack.Manifest("v1"); await pack.Install(m);
            foreach (var file in new[] { "saves/map/level.dat", "options.txt", "screenshots/shot.png", "ModularWarfare/packv4.json", "ModularWarfare/mod_config.json", "config/settings.cfg", "mods/user-added.jar" })
            { var p = pack.Game(file); Directory.CreateDirectory(Path.GetDirectoryName(p)!); await File.WriteAllTextAsync(p, "personal"); }
            pack.Network.Bytes = 0; pack.Data["mods/mwf.jar"] = "updated mwf"; pack.Data["config/settings.cfg"] = "new defaults";
            var next = pack.Manifest("v2"); await pack.Install(next); await pack.Service.Verify(next, default);
            Assert(pack.Network.Bytes == Encoding.UTF8.GetByteCount("updated mwf"));
            foreach (var file in new[] { "saves/map/level.dat", "options.txt", "screenshots/shot.png", "ModularWarfare/packv4.json", "ModularWarfare/mod_config.json", "config/settings.cfg", "mods/user-added.jar" }) Assert(await File.ReadAllTextAsync(pack.Game(file)) == "personal");
        });
        await Test("removed managed files leave new instance, old copy kept in rollback", async root => {
            using var pack = new Fixture(root); pack.Data["scripts/obsolete.zs"] = "old"; await pack.Install(pack.Manifest("v1"));
            pack.Data.Remove("scripts/obsolete.zs"); var next = pack.Manifest("v2"); await pack.Install(next);
            Assert(!File.Exists(pack.Game("scripts/obsolete.zs")));
            Assert(Directory.GetDirectories(Path.GetDirectoryName(pack.Service.Instance)!, ".files-backup-*").Any(d => File.Exists(Path.Combine(d, ".minecraft", "scripts", "obsolete.zs"))));
        });
        await Test("shared chunks deduplicate and empty files work", async root => {
            using var pack = new Fixture(root); pack.Data["scripts/copy.zs"] = pack.Data["mods/mwf.jar"]; pack.Data["scripts/empty.zs"] = ""; pack.Data["scripts/large.zs"] = new string('x', FileManifest.ChunkBytes) + "tail";
            var m = pack.Manifest("v1"); await pack.Install(m); Assert(new FileInfo(pack.Game("scripts/empty.zs")).Length == 0);
            Assert(pack.Network.Bytes == m.Files.Sum(f => f.Bytes) - Encoding.UTF8.GetByteCount(pack.Data["mods/mwf.jar"]));
        });
        await Test("corrupt response never damages working install", async root => {
            using var pack = new Fixture(root); var old = pack.Manifest("v1"); await pack.Install(old);
            pack.Data["mods/mwf.jar"] = "changed"; var next = pack.Manifest("v2"); pack.Network.Corrupt = true;
            await Reject(() => pack.Install(next)); pack.Network.Corrupt = false; await pack.Service.Verify(old, default);
            Assert(await File.ReadAllTextAsync(pack.Game("mods/mwf.jar")) == "mwf");
        });
        await Test("cancelled update retains original and refuses simultaneous install", async root => {
            using var pack = new Fixture(root); var m = pack.Manifest("v1"); await pack.Install(m);
            using var ct = new CancellationTokenSource(); ct.Cancel(); await Reject(() => pack.Service.Install(m with { Version = "v2" }, 4096, null, ct.Token));
            await pack.Service.Verify(m, default);
            using var gate = new FileStream(Path.Combine(root, ".install.lock"), FileMode.Open, FileAccess.ReadWrite, FileShare.None);
            await Reject(() => pack.Install(m));
        });
        await Test("corrupt local file and cache get repaired, no unverified launch", async root => {
            using var pack = new Fixture(root); var m = pack.Manifest("v1"); await pack.Install(m); await File.WriteAllTextAsync(pack.Game("mods/mwf.jar"), "bad");
            await Reject(() => pack.Service.Verify(m, default));
            var file = m.Files.Single(f => f.Path == "mods/mwf.jar"); await File.WriteAllTextAsync(Path.Combine(root, "file-cache", file.Chunks[0].Sha256), "bad");
            pack.Network.Bytes = 0; await pack.Install(m); Assert(pack.Network.Bytes == 3); await pack.Service.Verify(m, default);
        });
        await Test("interrupted directory swap recovers old instance", async root => {
            using var pack = new Fixture(root); var m = pack.Manifest("v1"); await pack.Install(m);
            var instances = Path.GetDirectoryName(pack.Service.Instance)!; var backup = ".files-backup-" + new string('a', 32); var stage = ".files-stage-" + new string('b', 32);
            Directory.Move(pack.Service.Instance, Path.Combine(instances, backup)); Directory.CreateDirectory(Path.Combine(instances, stage));
            await File.WriteAllTextAsync(Path.Combine(instances, ".files-update.json"), JsonSerializer.Serialize(new { Stage = stage, Backup = backup }));
            await pack.Service.Verify(m, default); Assert(Directory.Exists(pack.Service.Instance)); Assert(Directory.Exists(Path.Combine(instances, stage)));
        });
        await Test("unowned install and unsafe recovery journal fail closed", async root => {
            using var pack = new Fixture(root); Directory.CreateDirectory(pack.Service.Instance); await Reject(() => pack.Install(pack.Manifest("v1")));
            await File.WriteAllTextAsync(Path.Combine(Path.GetDirectoryName(pack.Service.Instance)!, ".files-update.json"), "{\"Stage\":\"../../outside\",\"Backup\":\"../../outside\"}");
            await Reject(() => pack.Install(pack.Manifest("v1")));
        });
        await Test("chunk URLs are project-bound and storage denial/redirect fails closed", async root => {
            var config = new AccountsConfig("https://test-project.supabase.co", "sb_publishable_test_public_only");
            foreach (var status in new[] { HttpStatusCode.Forbidden, HttpStatusCode.Redirect })
            {
                using var handler = new AuthHandler(status); using var api = new Accounts(config, handler);
                await api.SignIn("test@example.test", "test-password", default); var chunk = new FileChunk(new string('a', 64), 10); var spec = api.DevChunk(chunk);
                await Reject(() => api.OpenDevDownload(new Uri(spec.Url.Replace("test-project.supabase.co", "external.test")), default)); Assert(handler.Count == 1);
                await Reject(() => api.OpenDevDownload(new Uri(spec.Url), default)); Assert(handler.Count == 2);
            }
        });
        Assert(passed == 12); Console.WriteLine("PASS: 12 file update scenarios; no live full pack/game test");
    }
    private sealed class Fixture : IDisposable
    {
        public Dictionary<string, string> Data { get; } = new() { ["mods/metrocraft-port-test.jar"] = "minetro", ["mods/mwf.jar"] = "mwf", ["config/settings.cfg"] = "defaults" };
        public Handler Network { get; } = new();
        private readonly HttpClient http;
        public FileInstaller Service { get; }
        public Fixture(string root) { http = new(Network); Service = new(root, new Transfer(http), c => new("https://storage.test/" + c.Sha256, c.Sha256, c.Bytes)); }
        public string Game(string relative) => Check.Under(Path.Combine(Service.Instance, ".minecraft"), relative);
        public Task Install(FileManifest m) => Service.Install(m, 4096, null, default);
        public FileManifest Manifest(string version)
        {
            var files = new List<ManagedFile>();
            foreach (var pair in Data.OrderBy(p => p.Key, StringComparer.Ordinal))
            {
                var bytes = Encoding.UTF8.GetBytes(pair.Value); var sha = Sha(bytes); var chunks = new List<FileChunk>();
                for(int offset=0;offset<bytes.Length;offset+=FileManifest.ChunkBytes){var chunk=bytes.AsSpan(offset,Math.Min(FileManifest.ChunkBytes,bytes.Length-offset)).ToArray();var key=Sha(chunk);Network.Objects[key]=chunk;chunks.Add(new(key,chunk.Length));}
                files.Add(new(pair.Key, pair.Key.StartsWith("config/") ? "seed" : "managed", bytes.Length, sha, chunks));
            }
            return new() { Version = version, Files = files };
        }
        public void Dispose() => http.Dispose();
    }
    internal sealed class Handler : HttpMessageHandler
    {
        public Dictionary<string, byte[]> Objects { get; } = []; public long Bytes; public bool Corrupt;
        protected override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken ct)
        {
            ct.ThrowIfCancellationRequested(); var bytes = Objects[request.RequestUri!.Segments.Last()]; Bytes += bytes.Length;
            var data = bytes.ToArray(); if (Corrupt && data.Length > 0) data[0] ^= 0xff;
            return Task.FromResult(new HttpResponseMessage(HttpStatusCode.OK) { Content = new ByteArrayContent(data) });
        }
    }
    private sealed class AuthHandler(HttpStatusCode status) : HttpMessageHandler
    {
        public int Count;
        protected override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, CancellationToken ct)
        {
            Count++;
            return Task.FromResult(request.RequestUri!.AbsolutePath == "/auth/v1/token"
                ? new HttpResponseMessage(HttpStatusCode.OK) { Content = new StringContent("{\"access_token\":\"mock\",\"refresh_token\":\"refresh\",\"expires_in\":3600,\"user\":{\"id\":\"11111111-1111-4111-8111-111111111111\"}}") }
                : new HttpResponseMessage(status));
        }
    }
}
