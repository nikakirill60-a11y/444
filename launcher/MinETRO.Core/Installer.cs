using System.Diagnostics;
using System.Text.Json;

namespace MinETRO.Core;
public sealed class Installer(string root, Transfer transfer)
{
    public string Root { get; } = Path.GetFullPath(root);
    public string Data => Path.Combine(Root, "prism-data");
    public string Instance(PackManifest manifest) => Path.Combine(Data, "instances", manifest.InstanceId);
    public string EngineDirectory => Path.Combine(Root, "engine", "Prism-11.1.1");
    public string EngineExe => Path.Combine(EngineDirectory, "prismlauncher.exe");
    private FileStream Lock()
    {
        Directory.CreateDirectory(Root);
        try { return new FileStream(Path.Combine(Root, ".install.lock"), FileMode.OpenOrCreate, FileAccess.ReadWrite, FileShare.None); }
        catch (IOException) { throw new IOException("В этой папке уже идёт установка. Дождитесь её завершения."); }
    }
    public async Task PrepareEngine(PackManifest m, IProgress<TransferProgress>? progress, CancellationToken ct)
    {
        using var gate = Lock(); await Engine(m, progress, ct);
    }
    private async Task Engine(PackManifest m, IProgress<TransferProgress>? progress, CancellationToken ct)
    {
        m.Validate();
        if (File.Exists(EngineExe) && File.Exists(Path.Combine(EngineDirectory, ".minetro-engine.json"))) return;
        if (Directory.Exists(EngineDirectory)) throw new IOException("Папка движка уже существует, но установка не завершена. Выберите новую папку; существующие файлы не удаляются.");
        var zip = Path.Combine(Root, "cache", m.Prism.Sha256 + ".zip");
        progress?.Report(new("Подготовка официального Prism Launcher…")); await transfer.Download(m.Prism, zip, progress, ct);
        var stage = Path.Combine(Root, "engine", ".staging-" + Guid.NewGuid().ToString("N"));
        try
        {
            progress?.Report(new("Распаковка Prism…")); await Task.Run(() => SafeArchive.Extract(zip, stage, "", true, ct), ct);
            if (!File.Exists(Path.Combine(stage, "prismlauncher.exe"))) throw new InvalidDataException("В официальном архиве не найден Prism Launcher.");
            await File.WriteAllTextAsync(Path.Combine(stage, ".minetro-engine.json"), JsonSerializer.Serialize(m.Prism, PackManifest.Json), ct);
            ct.ThrowIfCancellationRequested(); Directory.Move(stage, EngineDirectory);
        }
        finally { if (Directory.Exists(stage)) Directory.Delete(stage, true); }
    }
    public async Task Verify(PackManifest m, CancellationToken ct)
    {
        m.Validate(); if (m.Archive == null) throw new InvalidDataException("Архив полной сборки пока не подключён.");
        var path = Instance(m); var receipt = Path.Combine(path, "minetro-install.json");
        if (!File.Exists(receipt)) throw new IOException("Сборка ещё не установлена.");
        var saved = PackManifest.Parse(await File.ReadAllTextAsync(receipt, ct));
        if (saved.Archive != m.Archive || saved.InstanceId != m.InstanceId || saved.RequiredFiles.Count != m.RequiredFiles.Count || !saved.RequiredFiles.SequenceEqual(m.RequiredFiles))
            throw new InvalidDataException("Манифест установленной сборки отличается. Выпустите новую версию, не перезаписывая существующую.");
        if (!File.Exists(Path.Combine(path, "instance.cfg")) || !File.Exists(Path.Combine(path, "mmc-pack.json"))) throw new IOException("Повреждён экземпляр Prism.");
        await VerifyFiles(Path.Combine(path, ".minecraft"), m, ct);
    }
    private static async Task VerifyFiles(string folder, PackManifest m, CancellationToken ct)
    {
        var mods = Path.Combine(folder, "mods");
        if (!Directory.Exists(mods) || Directory.GetFiles(mods, "*.jar").Length < 2)
            throw new InvalidDataException("Нужен архив полной сборки с модами, не один JAR MinETRO.");
        foreach (var file in m.RequiredFiles)
        {
            ct.ThrowIfCancellationRequested(); var path = Check.Under(folder, file.Path);
            if (!File.Exists(path) || await Transfer.Hash(path, ct) != file.Sha256.ToLowerInvariant())
                throw new InvalidDataException("Не прошла проверка файла: " + file.Path + ". Миры и настройки не изменены.");
        }
    }
    public async Task Install(PackManifest m, int memoryMb, IProgress<TransferProgress>? progress, CancellationToken ct)
    {
        m.Validate(); if (m.Archive == null) throw new InvalidDataException("Нужна ссылка на архив полной сборки.");
        if (memoryMb is < 2048 or > 16384) throw new InvalidDataException("ОЗУ должно быть от 2048 до 16384 МБ.");
        using var gate = Lock();
        if (Directory.Exists(Instance(m))) { progress?.Report(new("Проверка установленной сборки…")); await Verify(m, ct); await Engine(m, progress, ct); return; }
        var zip = Path.Combine(Root, "cache", m.Archive.Sha256.ToLowerInvariant() + ".zip");
        await transfer.Download(m.Archive, zip, progress, ct);
        var instances = Path.Combine(Data, "instances"); Directory.CreateDirectory(instances);
        var stage = Path.Combine(instances, ".staging-" + Guid.NewGuid().ToString("N"));
        try
        {
            progress?.Report(new("Распаковка и проверка сборки…"));
            var game = Path.Combine(stage, ".minecraft"); await Task.Run(() => SafeArchive.Extract(zip, game, m.ArchiveRoot, false, ct), ct);
            await VerifyFiles(game, m, ct);
            await File.WriteAllTextAsync(Path.Combine(stage, "mmc-pack.json"), JsonSerializer.Serialize(new
            {
                formatVersion = 1, components = new[] { new { uid = "net.minecraft", version = m.Minecraft }, new { uid = "net.minecraftforge", version = m.Forge } }
            }, PackManifest.Json), ct);
            await File.WriteAllTextAsync(Path.Combine(stage, "instance.cfg"), $"[General]\nConfigVersion=1.2\nInstanceType=OneSix\nname={m.Name}\nOverrideMemory=true\nMinMemAlloc=512\nMaxMemAlloc={memoryMb}\nOverrideJavaLocation=false\n", ct);
            await File.WriteAllTextAsync(Path.Combine(stage, "minetro-install.json"), JsonSerializer.Serialize(m, PackManifest.Json), ct);
            ct.ThrowIfCancellationRequested(); Directory.Move(stage, Instance(m));
        }
        finally { if (Directory.Exists(stage)) Directory.Delete(stage, true); }
        await Engine(m, progress, ct);
        progress?.Report(new("Сборка установлена. В Prism выберите учётную запись Microsoft и Java 8 (64-bit).", 100));
    }
    public ProcessStartInfo StartInfo(PackManifest m, bool launch)
    {
        if (!File.Exists(EngineExe)) throw new IOException("Сначала подготовьте Prism Launcher.");
        Directory.CreateDirectory(Data);
        var info = new ProcessStartInfo(EngineExe) { UseShellExecute = false, WorkingDirectory = EngineDirectory };
        info.ArgumentList.Add("--dir"); info.ArgumentList.Add(Data);
        if (launch) { info.ArgumentList.Add("--launch"); info.ArgumentList.Add(m.InstanceId); }
        return info;
    }
}
