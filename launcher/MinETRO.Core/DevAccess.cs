using System.Text.Json;

namespace MinETRO.Core;
public enum DevAccessState { Unknown, None, Pending, Approved, Rejected }
public enum PackChannel { Dev, Public }
public static class Channels
{
    public static string Name(PackChannel channel) => channel == PackChannel.Dev ? "MinETRO dev" : "MinETRO";
    public static bool CanInstall(PackChannel channel, DevAccessState access, PackManifest manifest) =>
        channel == PackChannel.Dev && access == DevAccessState.Approved && manifest.Archive != null;
    public static string Message(PackChannel channel, DevAccessState access, bool hasArchive) =>
        channel == PackChannel.Public ? "MinETRO пока недоступно." : access switch {
            DevAccessState.None => "Для MinETRO dev нужно одобрение администратора. Отправь заявку.",
            DevAccessState.Pending => "Заявка отправлена. Ожидает решения администратора в личном кабинете.",
            DevAccessState.Rejected => "Администратор отклонил заявку на MinETRO dev.",
            DevAccessState.Approved when hasArchive => "Доступ к MinETRO dev одобрен. Проверенная сборка готова к установке.",
            DevAccessState.Approved => "Доступ одобрен. Проверенный архив dev пока не подключён к загрузке.",
            _ => "Обнови статус доступа к MinETRO dev." };
}
public sealed partial class Accounts
{
    public async Task<DevAccessState> DevStatus(CancellationToken ct)
    {
        RequireDevSession();
        using var result = await Send(HttpMethod.Get, $"rest/v1/dev_access_requests?user_id=eq.{UserId:D}&select=status", null, true, ct);
        var rows = result.RootElement;
        if (rows.ValueKind != JsonValueKind.Array || rows.GetArrayLength() > 1) throw new AccountException("Неверный статус заявки dev.");
        return rows.GetArrayLength() == 0 ? DevAccessState.None : ParseDevStatus(rows[0].GetProperty("status").GetString());
    }
    public async Task<DevAccessState> RequestDev(CancellationToken ct)
    {
        RequireDevSession();
        using var result = await Send(HttpMethod.Post, "rest/v1/rpc/request_dev_access", new { }, true, ct);
        return ParseDevStatus(result.RootElement.GetString());
    }
    private static DevAccessState ParseDevStatus(string? status) => status switch {
        "pending" => DevAccessState.Pending, "approved" => DevAccessState.Approved, "rejected" => DevAccessState.Rejected,
        _ => throw new AccountException("Неверный статус заявки dev.") };
    private void RequireDevSession()
    {
        RequireUser();
        if (RecoverySession) throw new AccountException("Сначала закончи восстановление пароля и войди заново.");
    }
    public async Task<PackManifest?> DevRelease(CancellationToken ct)
    {
        RequireDevSession();
        // Authorization is enforced inside the database RPC, not by the desktop UI.
        using var result = await Send(HttpMethod.Post, "rest/v1/rpc/get_dev_release", new { }, true, ct);
        if (result.RootElement.ValueKind == JsonValueKind.Null) return null;
        try {
            var root = result.RootElement;
            var key = Check.Relative(root.GetProperty("objectKey").GetString() ?? "");
            var m = PackManifest.Parse(root.GetProperty("manifest").GetRawText());
            if (m.Archive == null) throw new AccountException("В релизе не указан архив dev.");
            // Never trust a release's arbitrary download URL. Only the fixed private
            // bucket of this same authenticated project may serve the pack.
            return m with { Name = "MinETRO dev", Archive = m.Archive with { Url = DevObjectUrl(key) } };
        }
        catch (Exception ex) when (ex is KeyNotFoundException or InvalidOperationException or JsonException) {
            throw new AccountException("Неверный ответ канала MinETRO dev.");
        }
    }
    private string DevObjectUrl(string key) => config.Url.TrimEnd('/') + "/storage/v1/object/authenticated/dev-packs/" +
        string.Join('/', key.Split('/').Select(Uri.EscapeDataString));
    public async Task<HttpResponseMessage> OpenDevDownload(Uri uri, CancellationToken ct)
    {
        RequireDevSession();
        // Re-read the approved release immediately before downloading. No bearer
        // token is ever passed to Transfer, a manifest, disk, GitHub, or redirects.
        var release = await DevRelease(ct);
        if (release?.Archive == null || uri.AbsoluteUri != release.Archive.Url) throw new AccountException("Архив недоступен или релиз изменился. Обнови статус.");
        return await Request(HttpMethod.Get, uri.PathAndQuery.TrimStart('/'), null, true, ct);
    }
}
