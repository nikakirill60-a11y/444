using System.Text.Json;
using System.Text.RegularExpressions;

namespace MinETRO.Core;

public sealed record DownloadSpec(string Url, string Sha256, long Bytes)
{
    public void Validate()
    {
        Check.Https(Url);
        if (!Regex.IsMatch(Sha256, "^[a-fA-F0-9]{64}$") || Bytes < 1 || Bytes > 8L * 1024 * 1024 * 1024)
            throw new InvalidDataException("Неверный размер или SHA256 загрузки.");
    }
}
public sealed record RequiredFile(string Path, string Sha256);
public sealed record PackManifest
{
    public int Schema { get; init; }
    public string Name { get; init; } = "";
    public string Version { get; init; } = "";
    public string Minecraft { get; init; } = "";
    public string Forge { get; init; } = "";
    public string Notice { get; init; } = "";
    public string ArchiveRoot { get; init; } = "";
    public DownloadSpec? Archive { get; init; }
    public List<RequiredFile> RequiredFiles { get; init; } = [];
    public string PrismVersion { get; init; } = "";
    public DownloadSpec Prism { get; init; } = new("", "", 0);
    public static readonly JsonSerializerOptions Json = new() { PropertyNameCaseInsensitive = true, PropertyNamingPolicy = JsonNamingPolicy.CamelCase, WriteIndented = true };
    public static PackManifest Parse(string text)
    {
        if (text.Length > 1024 * 1024) throw new InvalidDataException("Манифест слишком большой.");
        var m = JsonSerializer.Deserialize<PackManifest>(text, Json) ?? throw new InvalidDataException("Пустой манифест.");
        m.Validate(); return m;
    }
    public void Validate()
    {
        if (Schema != 1 || string.IsNullOrWhiteSpace(Name) || Name.Length > 100 || Name.Any(char.IsControl)
            || !Regex.IsMatch(Version, "^[A-Za-z0-9][A-Za-z0-9._-]{0,39}$")
            || Minecraft != "1.12.2" || Forge != "14.23.5.2860"
            || !Regex.IsMatch(PrismVersion, "^[0-9]+\\.[0-9]+\\.[0-9]+$"))
            throw new InvalidDataException("Неподдерживаемый манифест тестовой сборки 1.12.2.");
        if (ArchiveRoot.Length > 0) Check.Relative(ArchiveRoot.TrimEnd('/'));
        Prism.Validate(); Archive?.Validate();
        // Engine always comes from the pinned official Prism release, not a pack author's arbitrary EXE.
        if (PrismVersion != "11.1.1" || Prism != Engine)
            throw new InvalidDataException("Неизвестный движок Prism. Для смены движка нужен новый лаунчер.");
        if (RequiredFiles is null || RequiredFiles.Count > 5000) throw new InvalidDataException("Неверный список файлов.");
        var paths = new HashSet<string>(StringComparer.OrdinalIgnoreCase);
        foreach (var f in RequiredFiles)
        {
            Check.Relative(f.Path);
            if (!paths.Add(f.Path) || !Regex.IsMatch(f.Sha256, "^[a-fA-F0-9]{64}$")) throw new InvalidDataException("Неверная проверка файла.");
        }
        if (Archive != null && !RequiredFiles.Any(f => f.Path.StartsWith("mods/", StringComparison.OrdinalIgnoreCase) && f.Path.EndsWith(".jar", StringComparison.OrdinalIgnoreCase)))
            throw new InvalidDataException("Полная сборка должна содержать проверяемые моды, а не только ссылку на архив.");
    }
    public static DownloadSpec Engine => new("https://github.com/PrismLauncher/PrismLauncher/releases/download/11.1.1/PrismLauncher-Windows-MSVC-Portable-11.1.1.zip", "ab35a770fb06d89d2ccc098079db5db329fb4e68f42b72babd8b095efde3d2d7", 20396629);
    public string InstanceId => Archive == null ? "MinETRO-unconfigured" : "MinETRO-" + Version + "-" + Archive.Sha256[..12].ToLowerInvariant();
}
public static class Check
{
    public static Uri Https(string value)
    {
        if (!Uri.TryCreate(value, UriKind.Absolute, out var uri) || uri.Scheme != "https" || uri.UserInfo.Length != 0 || uri.Fragment.Length != 0)
            throw new InvalidDataException("Допускаются только прямые HTTPS-ссылки без пароля.");
        return uri;
    }
    public static string Relative(string value)
    {
        if (string.IsNullOrEmpty(value) || value.Length > 230 || value.Contains('\\') || value.StartsWith('/') || value.Any(c => c < 32 || ":*?\"<>|".Contains(c)))
            throw new InvalidDataException("Недопустимый путь внутри архива.");
        foreach (var part in value.Split('/'))
        {
            var stem = part.Split('.')[0].ToUpperInvariant();
            if (part.Length == 0 || part is "." or ".." || part.EndsWith('.') || part.EndsWith(' ') || stem is "CON" or "PRN" or "AUX" or "NUL" || Regex.IsMatch(stem, "^(COM|LPT)[0-9]$"))
                throw new InvalidDataException("Недопустимое имя файла Windows.");
        }
        return value;
    }
    public static string Under(string root, string relative)
    {
        Relative(relative);
        var full = Path.GetFullPath(Path.Combine(root, relative.Replace('/', Path.DirectorySeparatorChar)));
        var prefix = Path.GetFullPath(root).TrimEnd(Path.DirectorySeparatorChar) + Path.DirectorySeparatorChar;
        if (!full.StartsWith(prefix, StringComparison.OrdinalIgnoreCase)) throw new InvalidDataException("Выход за папку установки.");
        return full;
    }
}
