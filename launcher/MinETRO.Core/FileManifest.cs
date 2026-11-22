using System.Security.Cryptography;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Text.RegularExpressions;

namespace MinETRO.Core;

public sealed record FileChunk(string Sha256, long Bytes);
public sealed record ManagedFile(string Path, string Mode, long Bytes, string Sha256, List<FileChunk> Chunks);
// No URLs, credentials, executables, launcher components or mutable worlds in this format.
public sealed record FileManifest
{
    public int Schema { get; init; } = 2;
    public string Version { get; init; } = "";
    public List<ManagedFile> Files { get; init; } = [];
    public static readonly JsonSerializerOptions Json = new(PackManifest.Json) { WriteIndented = false, Encoder = System.Text.Encodings.Web.JavaScriptEncoder.UnsafeRelaxedJsonEscaping };
    public const int MaxJsonBytes = 64 * 1024 * 1024;
    public const int ChunkBytes = 8 * 1024 * 1024;
    public const string InstanceId = "MinETRO-dev-files";
    [JsonIgnore] public PackManifest EngineProfile => PackManifest.ReleaseDefault with { Name = "MinETRO dev", Version = Version };
    [JsonIgnore] public string Fingerprint => Convert.ToHexString(SHA256.HashData(Encoding.UTF8.GetBytes(JsonSerializer.Serialize(this, Json)))).ToLowerInvariant();
    public static FileManifest Parse(string json)
    {
        if (Encoding.UTF8.GetByteCount(json) > MaxJsonBytes) throw new InvalidDataException("Список файлов слишком большой.");
        var m = JsonSerializer.Deserialize<FileManifest>(json, PackManifest.Json) ?? throw new InvalidDataException("Пустой список файлов.");
        m.Validate(); return m;
    }
    public static bool HashValid(string? hash) => hash != null && Regex.IsMatch(hash, "^[a-f0-9]{64}$");
    public static bool Personal(string path) => path.Split('/').Any(p => p.Equals("packv4.json", StringComparison.OrdinalIgnoreCase)) ||
        path.Equals("ModularWarfare/mod_config.json", StringComparison.OrdinalIgnoreCase);
    public static bool ManagedPath(string path, string mode)
    {
        Check.Relative(path);
        if (Personal(path) || !path.Contains('/')) return false;
        var top = path.Split('/')[0];
        return mode == "seed" ? top == "config" : mode == "managed" && top is "mods" or "ModularWarfare" or "resourcepacks" or "scripts";
    }
    public void Validate()
    {
        if (Schema != 2 || !Regex.IsMatch(Version ?? "", "^[A-Za-z0-9][A-Za-z0-9._-]{0,39}$") || Files == null || Files.Count is < 2 or > 100000)
            throw new InvalidDataException("Неверный файловый релиз dev.");
        var paths = new HashSet<string>(StringComparer.OrdinalIgnoreCase);
        var sizes = new Dictionary<string, long>(); long total = 0; int mods = 0; bool minetro = false;
        foreach (var f in Files)
        {
            if (f == null || f.Path == null || f.Mode == null || !ManagedPath(f.Path, f.Mode) || !paths.Add(f.Path) || !HashValid(f.Sha256) || f.Bytes < 0 || f.Bytes > 8L * 1024 * 1024 * 1024 || f.Chunks == null || f.Chunks.Count > 1024)
                throw new InvalidDataException("Неверный или личный путь в файловом релизе.");
            long bytes = 0;
            foreach (var c in f.Chunks)
            {
                if (c == null || !HashValid(c.Sha256) || c.Bytes is < 1 or > ChunkBytes || (sizes.TryGetValue(c.Sha256, out var size) && size != c.Bytes))
                    throw new InvalidDataException("Неверный блок обновления.");
                sizes[c.Sha256] = c.Bytes; bytes += c.Bytes;
            }
            if (bytes != f.Bytes || (f.Bytes == 0 && f.Sha256 != Convert.ToHexString(SHA256.HashData(Array.Empty<byte>())).ToLowerInvariant())) throw new InvalidDataException("Размеры блоков не совпадают с файлом.");
            total += f.Bytes;
            if (f.Path.StartsWith("mods/", StringComparison.Ordinal) && f.Path.EndsWith(".jar", StringComparison.Ordinal)) { mods++; minetro |= f.Path.StartsWith("mods/metrocraft-port-", StringComparison.Ordinal); }
        }
        // File/directory collisions cannot reach staging, even on case-insensitive Windows.
        foreach (var path in paths)
            for (var dir = path.LastIndexOf('/'); dir > 0; dir = path.LastIndexOf('/', dir - 1))
                if (paths.Contains(path[..dir])) throw new InvalidDataException("Файл конфликтует с папкой.");
        if (mods < 2 || !minetro || total > 128L * 1024 * 1024 * 1024) throw new InvalidDataException("Нужна полная сборка MinETRO с модами.");
    }
}
