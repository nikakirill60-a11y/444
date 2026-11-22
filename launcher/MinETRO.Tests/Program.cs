using System.IO.Compression;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using MinETRO.Core;

var passed = 0;
async Task Test(string name, Func<Task> action) { await action(); Console.WriteLine("PASS " + name); passed++; }
void CheckTrue(bool ok) { if (!ok) throw new Exception("Assertion failed"); }
async Task Reject(Func<Task> action) { try { await action(); } catch (Exception e) when (e is InvalidDataException or IOException or OperationCanceledException) { return; } throw new Exception("Expected rejection"); }
string Sha(byte[] data) => Convert.ToHexString(SHA256.HashData(data)).ToLowerInvariant();
var scratch = Path.Combine(Path.GetTempPath(), "minetro-tests-" + Guid.NewGuid().ToString("N")); Directory.CreateDirectory(scratch);
string Zip(string name, params (string Path, string Body)[] files) {
    var path = Path.Combine(scratch, name + ".zip"); using var z = ZipFile.Open(path, ZipArchiveMode.Create);
    foreach (var (p, text) in files) { var entry = z.CreateEntry(p); using var w = new StreamWriter(entry.Open()); w.Write(text); }
    return path;
}
PackManifest Manifest(string zip, string version = "test-01") {
    var bytes = File.ReadAllBytes(zip);
    return new() { Schema = 1, Name = "MinETRO test", Version = version, Minecraft = "1.12.2", Forge = "14.23.5.2860", PrismVersion = "11.1.1", Prism = PackManifest.Engine,
        Archive = new("https://example.test/pack.zip", Sha(bytes), bytes.Length), RequiredFiles = [new("mods/one.jar", Sha(Encoding.UTF8.GetBytes("one")))] };
}
try {
await Test("default channel honestly unconfigured", () => {
    var m = PackManifest.Parse(File.ReadAllText(args[0])); CheckTrue(m.Archive == null); m.Validate(); return Task.CompletedTask;
});
await Test("path traversal / Windows reserved names / ADS rejected", async () => {
    foreach (var path in new[] { "../x", "/root", "C:/x", "a\\b", "a/../b", "a//b", "CON.txt", "NUL", "foo.", "foo ", "a\u0000", "x:evil", "LPT1.log" })
        await Reject(() => { Check.Relative(path); return Task.CompletedTask; });
    CheckTrue(Check.Under(scratch, "моды/pack v4.json").StartsWith(scratch));
});
await Test("manifest blocks arbitrary engine, HTTP, unsupported Minecraft", async () => {
    var m = PackManifest.Parse(File.ReadAllText(args[0]));
    foreach (var bad in new[] { m with { Prism = new("https://evil.test/app.zip", new string('a',64), 10) }, m with { Minecraft = "1.7.10" }, m with { Archive = new("http://example.test/a", new string('a',64),1) } })
        await Reject(() => { bad.Validate(); return Task.CompletedTask; });
});
await Test("safe extraction with archiveRoot and Unicode", () => {
    var zip = Zip("valid", ("outer/.minecraft/mods/тест.jar", "test"), ("outer/readme.txt", "ignored"));
    var dest = Path.Combine(scratch, "extract"); SafeArchive.Extract(zip, dest, "outer/.minecraft", false, default);
    CheckTrue(File.ReadAllText(Path.Combine(dest, "mods", "тест.jar")) == "test"); CheckTrue(!File.Exists(Path.Combine(dest,"readme.txt"))); return Task.CompletedTask;
});
await Test("zip-slip rejected before writing outside staging", async () => {
    var zip = Zip("slip", ("../escaped.txt", "bad")); await Reject(() => { SafeArchive.Extract(zip, Path.Combine(scratch,"slip"), "", false, default); return Task.CompletedTask; });
    CheckTrue(!File.Exists(Path.Combine(scratch,"escaped.txt")));
});
await Test("case-insensitive duplicate names rejected", async () => {
    var zip = Zip("duplicate", ("mods/A.jar","one"),("mods/a.jar","two")); await Reject(() => { SafeArchive.Extract(zip, Path.Combine(scratch,"dup"), "", false, default); return Task.CompletedTask; });
});
await Test("symlinks and credential files rejected", async () => {
    var zip = Zip("link", ("link", "outside")); using (var z = ZipFile.Open(zip, ZipArchiveMode.Update)) z.Entries[0].ExternalAttributes = unchecked((int)0xA1FF0000);
    await Reject(() => { SafeArchive.Extract(zip, Path.Combine(scratch,"link-dest"), "", false, default); return Task.CompletedTask; });
    foreach (var path in new[] { "accounts.json", "launcher_accounts.json", "run.cmd" }) {
        var file = Zip(Guid.NewGuid().ToString("N"), (path,"bad")); await Reject(() => { SafeArchive.Extract(file, Path.Combine(scratch,Guid.NewGuid().ToString("N")), "", false, default); return Task.CompletedTask; });
    }
});
await Test("SHA256 and exact length; corrupted cache not trusted", async () => {
    byte[] bytes = Encoding.UTF8.GetBytes("good bytes"); var calls = 0;
    using var http = new HttpClient(new Fake(_ => { calls++; return new(HttpStatusCode.OK) { Content = new ByteArrayContent(bytes) }; }));
    var transfer = new Transfer(http); var path = Path.Combine(scratch,"download.zip"); File.WriteAllText(path,"corrupt");
    await transfer.Download(new("https://example.test/file", Sha(bytes),bytes.Length),path,null,default); CheckTrue(File.ReadAllBytes(path).SequenceEqual(bytes));
    await transfer.Download(new("https://example.test/file", Sha(bytes),bytes.Length),path,null,default); CheckTrue(calls==1);
    await Reject(() => transfer.Download(new("https://example.test/file",new string('0',64),bytes.Length),Path.Combine(scratch,"bad.zip"),null,default));
    CheckTrue(!File.Exists(Path.Combine(scratch,"bad.zip"))); CheckTrue(Directory.GetFiles(scratch,"*.partial-*").Length==0);
});
await Test("HTTPS downgrade redirect rejected", async () => {
    using var http = new HttpClient(new Fake(_ => { var r = new HttpResponseMessage(HttpStatusCode.Redirect); r.Headers.Location=new Uri("http://example.test/plain"); return r; }));
    await Reject(async () => { using var r = await new Transfer(http).Open(new Uri("https://example.test/secure"),default); });
});
await Test("install is isolated, transactional, preserves saves and custom hands", async () => {
    var zip = Zip("pack", ("mods/one.jar","one"),("mods/two.jar","two"),("ModularWarfare/packv4.json","custom hands")); var m = Manifest(zip);
    var root = Path.Combine(scratch,"install root с пробелом"); var data = File.ReadAllBytes(zip);
    using var http = new HttpClient(new Fake(_ => new(HttpStatusCode.OK) { Content = new ByteArrayContent(data) }));
    var service = new Installer(root,new Transfer(http));
    // Engine is a stub fixture: never execute a binary in these tests.
    Directory.CreateDirectory(service.EngineDirectory); File.WriteAllText(service.EngineExe,"stub"); File.WriteAllText(Path.Combine(service.EngineDirectory,".minetro-engine.json"),"fixture");
    await service.Install(m,4096,null,default); await service.Verify(m,default);
    var game = Path.Combine(service.Instance(m),".minecraft"); Directory.CreateDirectory(Path.Combine(game,"saves")); File.WriteAllText(Path.Combine(game,"saves","world.txt"),"my world");
    File.WriteAllText(Path.Combine(game,"ModularWarfare","packv4.json"),"edited hands");
    await service.Install(m,4096,null,default); CheckTrue(File.ReadAllText(Path.Combine(game,"saves","world.txt"))=="my world"); CheckTrue(File.ReadAllText(Path.Combine(game,"ModularWarfare","packv4.json"))=="edited hands");
    var next = m with { Version = "test-02" }; await service.Install(next,4096,null,default);
    CheckTrue(service.Instance(m)!=service.Instance(next)); CheckTrue(File.Exists(Path.Combine(game,"saves","world.txt")));
    var start = service.StartInfo(m,true); CheckTrue(start.ArgumentList.SequenceEqual(new[]{"--dir",service.Data,"--launch",m.InstanceId})); CheckTrue(!start.UseShellExecute);
});
await Test("invalid pack never commits instance or erases prior instance", async () => {
    var zip = Zip("broken", ("mods/one.jar","wrong"),("mods/two.jar","two")); var m=Manifest(zip); var bytes=File.ReadAllBytes(zip);
    using var http = new HttpClient(new Fake(_ => new(HttpStatusCode.OK){Content=new ByteArrayContent(bytes)})); var service=new Installer(Path.Combine(scratch,"broken-root"),new Transfer(http));
    await Reject(() => service.Install(m,4096,null,default)); CheckTrue(!Directory.Exists(service.Instance(m)));
    CheckTrue(!Directory.EnumerateDirectories(Path.Combine(service.Data,"instances")).Any());
});
await Test("cancel leaves no final instance", async () => {
    var zip=Zip("cancel",("mods/one.jar","one"),("mods/two.jar","two")); var m=Manifest(zip); using var token=new CancellationTokenSource(); token.Cancel();
    using var http=new HttpClient(new Fake(_ => new(HttpStatusCode.OK){Content=new ByteArrayContent(File.ReadAllBytes(zip))}));
    var service=new Installer(Path.Combine(scratch,"cancel-root"),new Transfer(http)); await Reject(() => service.Install(m,4096,null,token.Token)); CheckTrue(!Directory.Exists(service.Instance(m)));
});
await Test("missing archive cannot install and cannot claim ready", async () => {
    var m=PackManifest.Parse(File.ReadAllText(args[0])); using var http=new HttpClient(new Fake(_ => throw new Exception("No network expected"))); var service=new Installer(Path.Combine(scratch,"empty"),new Transfer(http));
    await Reject(() => service.Install(m,4096,null,default)); await Reject(() => service.Verify(m,default));
});
await Test("confirmed channel survives restart; failed save retains previous snapshot", async () => {
    var zip=Zip("persist",("mods/one.jar","one"),("mods/two.jar","two")); var m=Manifest(zip);
    var store=new ChannelStore(Path.Combine(scratch,"preferences")); await store.Save(m,default);
    CheckTrue(new ChannelStore(Path.Combine(scratch,"preferences")).Load(m).InstanceId==m.InstanceId);
    using var cancel=new CancellationTokenSource(); cancel.Cancel();
    await Reject(() => store.Save(m with { Version="test-02" },cancel.Token));
    CheckTrue(store.Load(m).Version==m.Version);
});
await AccountsTests.Run();
await DevAccessTests.Run();
await FileUpdateTests.Run();
await ReleasesTests.Run();
if(args.Contains("--verify-accounts"))await ReleasesTests.Live(Path.Combine(Path.GetDirectoryName(args[0])!,"accounts.public.json"));

if (args.Contains("--verify-prism")) await Test("official pinned Prism download / SHA256 / archive layout", async () => {
    var m=PackManifest.Parse(File.ReadAllText(args[0])); using var http=Transfer.CreateClient();
    using var timeout=new CancellationTokenSource(TimeSpan.FromMinutes(5));
    var service=new Installer(Path.Combine(scratch,"official-engine"),new Transfer(http));
    await service.PrepareEngine(m,null,timeout.Token); CheckTrue(File.Exists(service.EngineExe));
    CheckTrue(service.StartInfo(m,false).ArgumentList.SequenceEqual(new[]{"--dir",service.Data}));
});
Console.WriteLine($"PASS: {passed} launcher core scenarios; real Minecraft/Microsoft login NOT run");
} finally { Directory.Delete(scratch,true); }
sealed class Fake(Func<HttpRequestMessage,HttpResponseMessage> make) : HttpMessageHandler
{
    protected override Task<HttpResponseMessage> SendAsync(HttpRequestMessage request,CancellationToken ct) { ct.ThrowIfCancellationRequested(); return Task.FromResult(make(request)); }
}
