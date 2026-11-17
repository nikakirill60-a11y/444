using System.Text.Json;
namespace MinETRO.Core;

/** A trusted, confirmed snapshot, not an automatic remote manifest fetch. */
public sealed class ChannelStore(string directory)
{
    private string FileName => Path.Combine(directory, "active-channel.json");
    public PackManifest Load(PackManifest fallback)
    {
        if (!File.Exists(FileName)) return fallback;
        if (new FileInfo(FileName).Length > 1024 * 1024) throw new InvalidDataException("Сохранённый канал слишком большой.");
        return PackManifest.Parse(File.ReadAllText(FileName));
    }
    public async Task Save(PackManifest manifest, CancellationToken ct)
    {
        manifest.Validate(); Directory.CreateDirectory(directory);
        var temporary = FileName + ".tmp-" + Guid.NewGuid().ToString("N");
        try
        {
            await File.WriteAllTextAsync(temporary, JsonSerializer.Serialize(manifest, PackManifest.Json), ct);
            ct.ThrowIfCancellationRequested(); File.Move(temporary, FileName, true);
        }
        finally { if (File.Exists(temporary)) File.Delete(temporary); }
    }
}
