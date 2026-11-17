using System.Net;
using System.Security.Cryptography;

namespace MinETRO.Core;
public sealed record TransferProgress(string Message, int? Percent = null);
public sealed class Transfer(HttpClient client)
{
    public static HttpClient CreateClient()
    {
        var result = new HttpClient(new HttpClientHandler { AllowAutoRedirect = false }) { Timeout = Timeout.InfiniteTimeSpan };
        result.DefaultRequestHeaders.UserAgent.ParseAdd("MinETRO-Launcher/0.4"); return result;
    }
    public static async Task<string> Hash(string file, CancellationToken ct)
    {
        await using var stream = File.OpenRead(file); return Convert.ToHexString(await SHA256.HashDataAsync(stream, ct)).ToLowerInvariant();
    }
    public async Task<HttpResponseMessage> Open(Uri uri, CancellationToken ct)
    {
        for (int n = 0; n < 6; n++)
        {
            using var request = new HttpRequestMessage(HttpMethod.Get, uri);
            using var headersTimeout = CancellationTokenSource.CreateLinkedTokenSource(ct); headersTimeout.CancelAfter(TimeSpan.FromSeconds(60));
            HttpResponseMessage response;
            try { response = await client.SendAsync(request, HttpCompletionOption.ResponseHeadersRead, headersTimeout.Token); }
            catch (OperationCanceledException) when (!ct.IsCancellationRequested) { throw new IOException("Сервер слишком долго не отвечает."); }
            if (response.StatusCode is HttpStatusCode.MovedPermanently or HttpStatusCode.Redirect or HttpStatusCode.SeeOther or HttpStatusCode.TemporaryRedirect or HttpStatusCode.PermanentRedirect)
            {
                var location = response.Headers.Location; response.Dispose();
                if (location == null) throw new IOException("Сервер не указал адрес загрузки.");
                uri = Check.Https(new Uri(uri, location).AbsoluteUri); continue;
            }
            if (!response.IsSuccessStatusCode) { var code = (int)response.StatusCode; response.Dispose(); throw new IOException($"Сервер вернул HTTP {code}."); }
            return response;
        }
        throw new IOException("Слишком много перенаправлений.");
    }
    public async Task<string> ReadManifest(string location, CancellationToken ct)
    {
        if (!location.StartsWith("https://", StringComparison.OrdinalIgnoreCase))
        {
            var file = new FileInfo(location);
            if (!file.Exists || file.Length > 1024 * 1024) throw new IOException("Манифест отсутствует или слишком большой.");
            return await File.ReadAllTextAsync(file.FullName, ct);
        }
        using var timeout = CancellationTokenSource.CreateLinkedTokenSource(ct); timeout.CancelAfter(TimeSpan.FromSeconds(45));
        using var response = await Open(Check.Https(location), timeout.Token);
        await using var stream = await response.Content.ReadAsStreamAsync(timeout.Token);
        using var data = new MemoryStream(); var buffer = new byte[8192]; int count;
        while ((count = await stream.ReadAsync(buffer, timeout.Token)) != 0)
        {
            if (data.Length + count > 1024 * 1024) throw new IOException("Манифест слишком большой.");
            data.Write(buffer, 0, count);
        }
        return System.Text.Encoding.UTF8.GetString(data.ToArray());
    }
    public async Task Download(DownloadSpec spec, string target, IProgress<TransferProgress>? progress, CancellationToken ct)
    {
        spec.Validate(); Directory.CreateDirectory(Path.GetDirectoryName(Path.GetFullPath(target))!);
        if (File.Exists(target) && new FileInfo(target).Length == spec.Bytes && await Hash(target, ct) == spec.Sha256.ToLowerInvariant()) return;
        var partial = target + ".partial-" + Guid.NewGuid().ToString("N");
        try
        {
            for (int attempt = 0; ; attempt++)
            {
                try
                {
                    using var response = await Open(Check.Https(spec.Url), ct);
                    if (response.Content.Headers.ContentLength is long length && length != spec.Bytes) throw new InvalidDataException("Размер загрузки не совпадает с манифестом.");
                    await using (var source = await response.Content.ReadAsStreamAsync(ct))
                    await using (var output = new FileStream(partial, FileMode.Create, FileAccess.Write, FileShare.None, 131072, true))
                    {
                        var buffer = new byte[131072]; long total = 0; var clock = System.Diagnostics.Stopwatch.StartNew();
                        while (true)
                        {
                            using var idle = CancellationTokenSource.CreateLinkedTokenSource(ct); idle.CancelAfter(TimeSpan.FromSeconds(60));
                            int count;
                            try { count = await source.ReadAsync(buffer, idle.Token); }
                            catch (OperationCanceledException) when (!ct.IsCancellationRequested) { throw new IOException("Загрузка зависла. Проверьте интернет."); }
                            if (count == 0) break;
                            total += count; if (total > spec.Bytes) throw new InvalidDataException("Сервер прислал лишние данные.");
                            await output.WriteAsync(buffer.AsMemory(0, count), ct);
                            if (clock.ElapsedMilliseconds > 150 || total == spec.Bytes) { clock.Restart(); progress?.Report(new($"Загрузка · {total / 1048576d:F1} / {spec.Bytes / 1048576d:F1} МБ", (int)(100 * total / spec.Bytes))); }
                        }
                        if (total != spec.Bytes) throw new IOException("Загрузка оборвалась. Повторите установку.");
                    }
                    if (await Hash(partial, ct) != spec.Sha256.ToLowerInvariant()) throw new InvalidDataException("SHA256 не совпал: файл не будет установлен.");
                    File.Move(partial, target, true); break;
                }
                catch (Exception ex) when (attempt < 2 && !ct.IsCancellationRequested && (ex is HttpRequestException or IOException) && ex is not InvalidDataException)
                { progress?.Report(new("Повтор загрузки…")); await Task.Delay(TimeSpan.FromSeconds(2 << attempt), ct); }
            }
        }
        finally { if (File.Exists(partial)) File.Delete(partial); }
    }
}
