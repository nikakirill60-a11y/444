using System.Net;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Text.RegularExpressions;

namespace MinETRO.Core;

// Only a public anon/publishable key belongs in a desktop application. Never a service role key.
public sealed record AccountsConfig(string Url = "", string PublicKey = "", bool EmailCodesEnabled = false)
{
    public bool Enabled => !string.IsNullOrWhiteSpace(Url) && !string.IsNullOrWhiteSpace(PublicKey);
    public static AccountsConfig Load(string path)
    {
        var config = JsonSerializer.Deserialize<AccountsConfig>(File.ReadAllText(path), Accounts.Json) ?? throw new InvalidDataException("Пустая конфигурация аккаунтов.");
        config.Validate(); return config;
    }
    public void Validate()
    {
        if (Url == "" && PublicKey == "") return;
        if (!Enabled || !Uri.TryCreate(Url, UriKind.Absolute, out var uri) || uri.Scheme != "https" || !uri.IsDefaultPort ||
            uri.UserInfo != "" || uri.AbsolutePath != "/" || uri.Query != "" || uri.Fragment != "" ||
            !Regex.IsMatch(uri.Host, @"^[a-z0-9-]+\.supabase\.co$", RegexOptions.CultureInvariant))
            throw new InvalidDataException("Нужен HTTPS-адрес проекта Supabase и публичный ключ.");
        if (PublicKey.StartsWith("sb_publishable_", StringComparison.Ordinal) && Regex.IsMatch(PublicKey, @"^sb_publishable_[A-Za-z0-9_-]+$")) return;
        try
        {
            var parts = PublicKey.Split('.'); if (parts.Length != 3) throw new FormatException();
            string body = parts[1].Replace('-', '+').Replace('_', '/'); body = body.PadRight((body.Length + 3) / 4 * 4, '=');
            using var json = JsonDocument.Parse(Convert.FromBase64String(body));
            if (json.RootElement.GetProperty("role").GetString() == "anon") return;
        }
        catch (Exception e) when (e is FormatException or JsonException or KeyNotFoundException) { }
        throw new InvalidDataException("В лаунчере разрешён только anon / publishable ключ. Секретные ключи запрещены.");
    }
}

public sealed record PlayerProfile(Guid Id, string Nickname, [property: JsonPropertyName("skin_path")] string? SkinPath, [property: JsonPropertyName("skin_model")] string SkinModel);
public sealed class AccountException(string message) : IOException(message);

// Sessions intentionally live only in memory. No plaintext password, access or refresh token files.
public sealed class Accounts : IDisposable
{
    public static readonly JsonSerializerOptions Json = new() { PropertyNameCaseInsensitive = true };
    private readonly HttpClient http;
    private readonly AccountsConfig config;
    private string? accessToken, refreshToken;
    private DateTimeOffset expires;
    public Guid? UserId { get; private set; }
    public bool RecoverySession { get; private set; }
    public bool SignedIn => UserId != null;
    public Accounts(AccountsConfig config, HttpMessageHandler? handler = null)
    {
        config.Validate(); this.config = config;
        http = new HttpClient(handler ?? new HttpClientHandler { AllowAutoRedirect = false, UseCookies = false }) { Timeout = TimeSpan.FromSeconds(30) };
    }
    public static void ValidateNickname(string nick)
    {
        if (!Regex.IsMatch(nick, @"^[A-Za-z0-9_]{3,16}$", RegexOptions.CultureInvariant)) throw new AccountException("Ник: 3–16 латинских букв, цифр или знаков _. ");
    }
    public static void ValidatePassword(string password)
    {
        if (password.Length < 12 || password.Length > 128) throw new AccountException("Пароль должен содержать от 12 до 128 символов.");
    }
    public static string ValidateEmail(string email)
    {
        var clean = email.Trim();
        if (clean.Length > 254 || !System.Net.Mail.MailAddress.TryCreate(clean, out var address) || address.Address != clean || !clean.Contains('@'))
            throw new AccountException("Проверь адрес электронной почты.");
        return clean;
    }
    public async Task SignUp(string email, string password, string nickname, CancellationToken ct)
    {
        ValidatePassword(password); ValidateNickname(nickname);
        using var result = await Send(HttpMethod.Post, "auth/v1/signup", new { email = ValidateEmail(email), password, data = new { nickname } }, false, ct);
        // Require explicit sign-in even when an administrator disabled email confirmation.
    }
    public async Task SignIn(string email, string password, CancellationToken ct)
    {
        Clear();
        using var result = await Send(HttpMethod.Post, "auth/v1/token?grant_type=password", new { email = ValidateEmail(email), password }, false, ct);
        AcceptSession(result.RootElement, false);
    }
    public async Task ResendConfirmation(string email, CancellationToken ct)
    {
        using var result = await Send(HttpMethod.Post, "auth/v1/resend", new { email = ValidateEmail(email), type = "signup" }, false, ct);
    }
    public async Task SendRecovery(string email, CancellationToken ct)
    {
        using var result = await Send(HttpMethod.Post, "auth/v1/recover", new { email = ValidateEmail(email) }, false, ct);
    }
    public async Task VerifyCode(string email, string code, bool recovery, CancellationToken ct)
    {
        if (!Regex.IsMatch(code.Trim(), @"^\d{6,10}$", RegexOptions.CultureInvariant)) throw new AccountException("Введи цифровой код из письма.");
        Clear();
        using var result = await Send(HttpMethod.Post, "auth/v1/verify", new { email = ValidateEmail(email), token = code.Trim(), type = recovery ? "recovery" : "signup" }, false, ct);
        AcceptSession(result.RootElement, recovery);
    }
    public async Task SetRecoveredPassword(string password, CancellationToken ct)
    {
        if (!RecoverySession) throw new AccountException("Сначала подтверди код восстановления из письма.");
        ValidatePassword(password);
        using var result = await Send(HttpMethod.Put, "auth/v1/user", new { password }, true, ct);
        await SignOut(ct, true);
    }
    public async Task<PlayerProfile> Profile(CancellationToken ct)
    {
        RequireUser();
        using var result = await Send(HttpMethod.Get, $"rest/v1/profiles?id=eq.{UserId:D}&select=id,nickname,skin_path,skin_model", null, true, ct);
        var profiles = result.RootElement.Deserialize<PlayerProfile[]>(Json);
        if (profiles?.Length != 1 || profiles[0].Id != UserId) throw new AccountException("Профиль не найден. Проверь миграцию Supabase.");
        return profiles[0];
    }
    public async Task Rename(string nickname, CancellationToken ct)
    {
        RequireUser(); ValidateNickname(nickname);
        using var result = await Send(HttpMethod.Patch, $"rest/v1/profiles?id=eq.{UserId:D}", new { nickname }, true, ct);
    }
    public static void ValidateSkin(byte[] png)
    {
        // Check dimensions before a decoder allocates memory. UI decodes and re-encodes to strip metadata.
        if (png.Length < 33 || png.Length > 256 * 1024 || !png.AsSpan(0, 8).SequenceEqual(new byte[] {137,80,78,71,13,10,26,10}) ||
            !png.AsSpan(12, 4).SequenceEqual("IHDR"u8) || System.Buffers.Binary.BinaryPrimitives.ReadInt32BigEndian(png.AsSpan(16,4)) != 64 ||
            System.Buffers.Binary.BinaryPrimitives.ReadInt32BigEndian(png.AsSpan(20,4)) is not (32 or 64))
            throw new AccountException("Нужен PNG-скин 64×64 или 64×32, не больше 256 КБ.");
    }
    public async Task UploadSkin(byte[] png, string model, CancellationToken ct)
    {
        RequireUser(); ValidateSkin(png);
        if (model is not ("steve" or "alex")) throw new AccountException("Неизвестная модель скина.");
        if (model == "alex" && System.Buffers.Binary.BinaryPrimitives.ReadInt32BigEndian(png.AsSpan(20,4)) == 32) throw new AccountException("Для Alex нужен скин 64×64.");
        // One fixed path per user avoids orphan accumulation; a failed update can be retried safely.
        var path = $"{UserId:D}/skin.png";
        using (var content = new ByteArrayContent(png))
        {
            content.Headers.ContentType = new MediaTypeHeaderValue("image/png");
            using var result = await Send(HttpMethod.Post, "storage/v1/object/skins/" + path, content, true, ct, true);
        }
        using var update = await Send(HttpMethod.Patch, $"rest/v1/profiles?id=eq.{UserId:D}", new { skin_path = path, skin_model = model }, true, ct);
    }
    public async Task<byte[]?> DownloadSkin(PlayerProfile profile, CancellationToken ct)
    {
        RequireUser();
        if (profile.SkinPath == null) return null;
        if (profile.Id != UserId || profile.SkinPath != $"{UserId:D}/skin.png") throw new AccountException("Недопустимый путь скина.");
        using var response = await Request(HttpMethod.Get, "storage/v1/object/authenticated/skins/" + profile.SkinPath, null, true, ct);
        return await ReadLimited(response, 256 * 1024, ct);
    }
    public async Task SignOut(CancellationToken ct, bool allSessions = false)
    {
        try { if (SignedIn) { using var result = await Send(HttpMethod.Post, "auth/v1/logout?scope=" + (allSessions ? "global" : "local"), null, true, ct); } }
        finally { Clear(); }
    }
    private void RequireUser() { if (!SignedIn) throw new AccountException("Сначала войди в аккаунт MinETRO."); }
    private void AcceptSession(JsonElement root, bool recovery)
    {
        try
        {
            var id = root.GetProperty("user").GetProperty("id").GetGuid();
            var token = root.GetProperty("access_token").GetString(); var refresh = root.GetProperty("refresh_token").GetString();
            var seconds = root.GetProperty("expires_in").GetInt32();
            if (id == Guid.Empty || string.IsNullOrWhiteSpace(token) || string.IsNullOrWhiteSpace(refresh) || seconds < 1) throw new FormatException();
            UserId = id; accessToken = token; refreshToken = refresh; expires = DateTimeOffset.UtcNow.AddSeconds(seconds); RecoverySession = recovery;
        }
        catch (Exception e) when (e is KeyNotFoundException or InvalidOperationException or FormatException)
        { Clear(); throw new AccountException("Сервис не вернул действительный сеанс. Войди заново."); }
    }
    private async Task EnsureSession(CancellationToken ct)
    {
        RequireUser(); if (expires > DateTimeOffset.UtcNow.AddSeconds(60)) return;
        var id = UserId; var recovery = RecoverySession;
        try
        {
            using var result = await Send(HttpMethod.Post, "auth/v1/token?grant_type=refresh_token", new { refresh_token = refreshToken }, false, ct);
            AcceptSession(result.RootElement, recovery);
            if (id != UserId) { Clear(); throw new AccountException("Пользователь сеанса изменился. Войди заново."); }
        }
        catch (AccountException) { Clear(); throw; }
    }
    private async Task<JsonDocument> Send(HttpMethod method, string path, object? body, bool auth, CancellationToken ct, bool upsert = false)
    {
        using var response = await Request(method, path, body, auth, ct, upsert);
        var bytes = await ReadLimited(response, 512 * 1024, ct);
        try { return JsonDocument.Parse(bytes.Length == 0 ? "{}"u8.ToArray() : bytes); }
        catch (JsonException) { throw new AccountException("Неверный ответ сервиса аккаунтов."); }
    }
    private async Task<HttpResponseMessage> Request(HttpMethod method, string path, object? body, bool auth, CancellationToken ct, bool upsert = false)
    {
        if (!config.Enabled) throw new AccountException("Аккаунты пока не подключены. Ожидается настройка Supabase.");
        if (auth) await EnsureSession(ct);
        using var request = new HttpRequestMessage(method, config.Url.TrimEnd('/') + "/" + path);
        request.Headers.Add("apikey", config.PublicKey);
        if (auth) request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", accessToken);
        if (upsert) request.Headers.Add("x-upsert", "true");
        if (body != null) request.Content = body as HttpContent ?? JsonContent.Create(body);
        var response = await http.SendAsync(request, HttpCompletionOption.ResponseHeadersRead, ct);
        if (!response.IsSuccessStatusCode)
        {
            var status = response.StatusCode; response.Dispose();
            if (auth && status == HttpStatusCode.Unauthorized) Clear();
            // Do not show raw API responses: they may contain tokens, emails or internal SQL details.
            throw new AccountException(status switch {
                HttpStatusCode.TooManyRequests => "Слишком много попыток. Подожди перед повтором.",
                HttpStatusCode.Conflict => "Этот ник уже занят или данные конфликтуют.",
                HttpStatusCode.Unauthorized or HttpStatusCode.Forbidden => "Доступ отклонён. Проверь вход, код подтверждения и настройку сервиса.",
                HttpStatusCode.BadRequest or HttpStatusCode.UnprocessableEntity => "Проверь введённые данные, подтверждение почты и доступность ника.",
                _ => "Сервис аккаунтов недоступен. Попробуй позже." });
        }
        return response;
    }
    private static async Task<byte[]> ReadLimited(HttpResponseMessage response, int limit, CancellationToken ct)
    {
        if (response.Content.Headers.ContentLength > limit) throw new AccountException("Ответ сервиса слишком большой.");
        using var input = await response.Content.ReadAsStreamAsync(ct); using var output = new MemoryStream(); var buffer = new byte[8192];
        int n; while ((n = await input.ReadAsync(buffer, ct)) != 0) { if (output.Length + n > limit) throw new AccountException("Ответ сервиса слишком большой."); await output.WriteAsync(buffer.AsMemory(0,n), ct); }
        return output.ToArray();
    }
    private void Clear() { UserId = null; accessToken = refreshToken = null; expires = default; RecoverySession = false; }
    public void Dispose() { Clear(); http.Dispose(); }
}
