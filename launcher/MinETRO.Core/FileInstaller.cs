using System.Diagnostics;
using System.Text.Json;
using System.Text.RegularExpressions;

namespace MinETRO.Core;

// Content-addressed network cache + copy-on-stage update. Never patches live game data.
// Previous instance remains in .files-backup-* until the owner removes it manually.
public sealed class FileInstaller(string root, Transfer transfer, Func<FileChunk, DownloadSpec> resolve)
{
    private readonly string root = Path.GetFullPath(root);
    private string Instances => Path.Combine(root, "prism-data", "instances");
    public string Instance => Path.Combine(Instances, FileManifest.InstanceId);
    private string Journal => Path.Combine(Instances, ".files-update.json");
    private const string Receipt = "minetro-files.json";
    private sealed record Swap(string Stage, string Backup);
    private FileStream Lock()
    {
        NoLinks(root); Directory.CreateDirectory(root); NoLinks(Path.Combine(root, ".install.lock"));
        try { return new FileStream(Path.Combine(root, ".install.lock"), FileMode.OpenOrCreate, FileAccess.ReadWrite, FileShare.None); }
        catch (IOException) { throw new IOException("В этой папке уже идёт установка."); }
    }
    private static void NoLinks(string path)
    {
        for (var p = new DirectoryInfo(Path.GetFullPath(path)); p != null; p = p.Parent)
            if (p.Exists && p.Attributes.HasFlag(FileAttributes.ReparsePoint)) throw new IOException("Ссылки и junction в папке установки запрещены.");
        if (File.Exists(path) && File.GetAttributes(path).HasFlag(FileAttributes.ReparsePoint)) throw new IOException("Ссылка вместо файла.");
    }
    private static async Task<bool> Matches(string path, long bytes, string sha, CancellationToken ct)
    {
        NoLinks(path);
        return File.Exists(path) && new FileInfo(path).Length == bytes && await Transfer.Hash(path, ct) == sha;
    }
    private static void CopyTree(string source, string destination, CancellationToken ct)
    {
        ct.ThrowIfCancellationRequested(); NoLinks(source); Directory.CreateDirectory(destination);
        foreach (var file in Directory.EnumerateFiles(source)) { ct.ThrowIfCancellationRequested(); NoLinks(file); File.Copy(file, Path.Combine(destination, Path.GetFileName(file))); }
        foreach (var dir in Directory.EnumerateDirectories(source)) CopyTree(dir, Path.Combine(destination, Path.GetFileName(dir)), ct);
    }
    private void Recover()
    {
        NoLinks(Instances); NoLinks(Journal); NoLinks(Instance);
        if (!File.Exists(Journal)) return;
        var journalFile = new FileInfo(Journal);
        if (journalFile.Length > 1024) throw new IOException("Повреждён журнал обновления.");
        var swap = JsonSerializer.Deserialize<Swap>(File.ReadAllText(Journal)) ?? throw new IOException("Повреждён журнал обновления.");
        if (!Regex.IsMatch(swap.Stage, "^\\.files-stage-[a-f0-9]{32}$") || !Regex.IsMatch(swap.Backup, "^\\.files-backup-[a-f0-9]{32}$")) throw new IOException("Небезопасный журнал обновления.");
        var stage = Path.Combine(Instances, swap.Stage); var backup = Path.Combine(Instances, swap.Backup);
        NoLinks(stage); NoLinks(backup);
        if (!Directory.Exists(Instance) && Directory.Exists(backup)) Directory.Move(backup, Instance);
        // A verified previous install is restored if the process stopped between renames.
        // Keep abandoned stages (may contain user data); never recursively delete from a journal.
        File.Delete(Journal);
    }
    public async Task Verify(FileManifest manifest, CancellationToken ct)
    {
        using var gate = Lock(); Recover(); await VerifyAt(Instance, manifest, ct);
    }
    private static async Task VerifyAt(string instance, FileManifest manifest, CancellationToken ct)
    {
        manifest.Validate(); NoLinks(instance);
        var receipt = Path.Combine(instance, Receipt); NoLinks(receipt);
        if (!File.Exists(receipt) || new FileInfo(receipt).Length > FileManifest.MaxJsonBytes || FileManifest.Parse(await File.ReadAllTextAsync(receipt, ct)).Fingerprint != manifest.Fingerprint)
            throw new IOException("Нужно установить файловое обновление dev.");
        foreach (var f in manifest.Files.Where(f => f.Mode == "managed"))
            if (!await Matches(Check.Under(Path.Combine(instance, ".minecraft"), f.Path), f.Bytes, f.Sha256, ct)) throw new IOException("Повреждён файл: " + f.Path);
        foreach (var file in new[] { "instance.cfg", "mmc-pack.json" })
            if (!File.Exists(Path.Combine(instance, file))) throw new IOException("Повреждён экземпляр Prism.");
    }
    public async Task Install(FileManifest manifest, int memoryMb, IProgress<TransferProgress>? progress, CancellationToken ct)
    {
        manifest.Validate();
        if (memoryMb is < 2048 or > 16384) throw new InvalidDataException("Неверный объём ОЗУ.");
        using var gate = Lock(); Directory.CreateDirectory(Instances); Recover();
        FileManifest? previous = null;
        if (Directory.Exists(Instance))
        {
            var receipt = Path.Combine(Instance, Receipt); NoLinks(receipt);
            if (!File.Exists(receipt) || new FileInfo(receipt).Length > FileManifest.MaxJsonBytes) throw new IOException("Папка dev не принадлежит файловому установщику. Выбери другую папку.");
            previous = FileManifest.Parse(await File.ReadAllTextAsync(receipt, ct));
        }
        string stage = Path.Combine(Instances, ".files-stage-" + Guid.NewGuid().ToString("N"));
        string backup = Path.Combine(Instances, ".files-backup-" + Guid.NewGuid().ToString("N"));
        Directory.CreateDirectory(stage);
        bool journaled = false;
        try
        {
            progress?.Report(new("Подготовка обновления. Закрой Minecraft перед установкой."));
            if (previous != null) await Task.Run(() => CopyTree(Instance, stage, ct), ct);
            var game = Path.Combine(stage, ".minecraft"); Directory.CreateDirectory(game);
            var keep = manifest.Files.Select(f => f.Path).ToHashSet(StringComparer.OrdinalIgnoreCase);
            // Only previously managed, now removed files are removed FROM THE STAGE.
            // Their original copies remain intact in the rollback instance.
            if (previous != null)
                foreach (var f in previous.Files.Where(f => f.Mode == "managed" && !keep.Contains(f.Path)))
                { var old = Check.Under(game, f.Path); NoLinks(old); if (File.Exists(old)) File.Delete(old); }
            int done = 0;
            foreach (var f in manifest.Files)
            {
                ct.ThrowIfCancellationRequested(); var target = Check.Under(game, f.Path); NoLinks(target);
                if ((f.Mode == "seed" && File.Exists(target)) || await Matches(target, f.Bytes, f.Sha256, ct)) { done++; continue; }
                var assembled = Path.Combine(stage, ".assembled");
                await using (var output = new FileStream(assembled, FileMode.Create, FileAccess.Write, FileShare.None, 131072, true))
                {
                    foreach (var chunk in f.Chunks)
                    {
                        var spec = resolve(chunk);
                        if (spec.Bytes != chunk.Bytes || spec.Sha256 != chunk.Sha256) throw new InvalidDataException("Неверное разрешение блока.");
                        var cache = Path.Combine(root, "file-cache", chunk.Sha256); NoLinks(cache);
                        await transfer.Download(spec, cache, progress, ct);
                        await using var input = File.OpenRead(cache); await input.CopyToAsync(output, ct);
                    }
                }
                if (!await Matches(assembled, f.Bytes, f.Sha256, ct)) throw new InvalidDataException("SHA256 собранного файла не совпал: " + f.Path);
                Directory.CreateDirectory(Path.GetDirectoryName(target)!); File.Move(assembled, target, true);
                progress?.Report(new("Готово файлов: " + (++done) + " / " + manifest.Files.Count, 100 * done / manifest.Files.Count));
            }
            await File.WriteAllTextAsync(Path.Combine(stage, Receipt), JsonSerializer.Serialize(manifest, FileManifest.Json), ct);
            if (previous == null)
            {
                await File.WriteAllTextAsync(Path.Combine(stage, "instance.cfg"), $"[General]\nConfigVersion=1.2\nInstanceType=OneSix\nname=MinETRO dev\nOverrideMemory=true\nMinMemAlloc=512\nMaxMemAlloc={memoryMb}\nOverrideJavaLocation=false\n", ct);
                await File.WriteAllTextAsync(Path.Combine(stage, "mmc-pack.json"), JsonSerializer.Serialize(new { formatVersion = 1, components = new[] { new { uid = "net.minecraft", version = "1.12.2" }, new { uid = "net.minecraftforge", version = "14.23.5.2860" } } }), ct);
            }
            await VerifyAt(stage, manifest, ct); ct.ThrowIfCancellationRequested();
            // Cancellation is not observed during the short commit/recovery section.
            using (var output = new FileStream(Journal, FileMode.CreateNew, FileAccess.Write, FileShare.None))
            { JsonSerializer.Serialize(output, new Swap(Path.GetFileName(stage), Path.GetFileName(backup))); output.Flush(true); }
            journaled = true;
            if (Directory.Exists(Instance)) Directory.Move(Instance, backup);
            Directory.Move(stage, Instance); File.Delete(Journal); journaled = false;
            progress?.Report(new("Файловое обновление установлено. Миры и настройки сохранены.", 100));
        }
        catch { if (journaled) Recover(); throw; }
        finally { if (!journaled && Directory.Exists(stage)) Directory.Delete(stage, true); }
    }
    public ProcessStartInfo StartInfo(Installer engine, FileManifest manifest)
    {
        var result = engine.StartInfo(manifest.EngineProfile, false);
        result.ArgumentList.Add("--launch"); result.ArgumentList.Add(FileManifest.InstanceId); return result;
    }
}
