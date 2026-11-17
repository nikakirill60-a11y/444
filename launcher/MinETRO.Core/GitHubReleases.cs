using System.Text.Json;

namespace MinETRO.Core;

// Fixed first-party repository. Users never have to paste a channel URL or select a JSON file.
public sealed class GitHubReleases(Transfer transfer)
{
    public const string Repository = "nikakirill60-a11y/444";
    public const string Page = "https://github.com/" + Repository + "/releases";
    public const string Api = "https://api.github.com/repos/" + Repository + "/releases?per_page=100";
    public const string MetadataAsset = "MinETRO-Modpack.json";
    public const string ArchiveAsset = "MinETRO-Modpack.zip";
    public async Task<PackManifest?> Latest(CancellationToken ct)
    {
        using var json = JsonDocument.Parse(await transfer.ReadManifest(Api,ct));
        if(json.RootElement.ValueKind != JsonValueKind.Array) throw new InvalidDataException("GitHub вернул неверный список релизов.");
        // Launcher/JAR-only releases cannot accidentally be installed as a complete modpack.
        foreach(var release in json.RootElement.EnumerateArray().Where(r=>!r.GetProperty("draft").GetBoolean() && !r.GetProperty("prerelease").GetBoolean()).OrderByDescending(r=>r.GetProperty("published_at").GetDateTimeOffset()))
        {
            var tag=release.GetProperty("tag_name").GetString() ?? "";
            var assets=release.GetProperty("assets").EnumerateArray().ToArray();
            var metadata=assets.Where(a=>a.GetProperty("name").GetString()==MetadataAsset).ToArray();
            if(metadata.Length==0) continue;
            var packs=assets.Where(a=>a.GetProperty("name").GetString()==ArchiveAsset).ToArray();
            if(metadata.Length!=1 || packs.Length!=1) throw new InvalidDataException("Релиз полной сборки оформлен неверно: ожидаются ZIP и файл проверки.");
            string Url(JsonElement asset,string name)
            {
                var url=asset.GetProperty("browser_download_url").GetString() ?? "";
                var expected=$"https://github.com/{Repository}/releases/download/{Uri.EscapeDataString(tag)}/{name}";
                if(!Uri.TryCreate(url,UriKind.Absolute,out var uri) || uri.AbsoluteUri!=new Uri(expected).AbsoluteUri)
                    throw new InvalidDataException("Файлы сборки должны находиться в том же GitHub-релизе MinETRO.");
                return url;
            }
            var metadataUrl=Url(metadata[0],MetadataAsset);var packUrl=Url(packs[0],ArchiveAsset);
            if(metadata[0].GetProperty("size").GetInt64() is < 1 or > 1048576) throw new InvalidDataException("Файл проверки релиза слишком большой.");
            var manifest=PackManifest.Parse(await transfer.ReadManifest(metadataUrl,ct));
            if(manifest.Archive==null || manifest.Archive.Url!=packUrl || manifest.Archive.Bytes!=packs[0].GetProperty("size").GetInt64())
                throw new InvalidDataException("Данные проверки не соответствуют ZIP из GitHub Releases.");
            if(packs[0].TryGetProperty("digest",out var digest) && digest.ValueKind==JsonValueKind.String &&
                !string.Equals(digest.GetString(),"sha256:"+manifest.Archive.Sha256,StringComparison.OrdinalIgnoreCase))
                throw new InvalidDataException("SHA256 не соответствует GitHub Release asset digest.");
            return manifest;
        }
        return null;
    }
}
