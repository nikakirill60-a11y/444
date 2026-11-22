// Same public project/key and profile schema as launcher/accounts.public.json.
// Public publishable key only. Passwords and sessions never go into browser storage.
export const AUTH_URL = 'https://xmhxucddhkitxqtweifu.supabase.co';
export const PUBLIC_KEY = 'sb_publishable_8Ih_1-oiz9jihSURfZ5nTQ_bBFFdBAZ';
export const CONFIRM_URL = 'https://minetro-beta.vercel.app/account.html';
export function validate(email, password, nickname) {
  email = email.trim();
  if (email.length > 254 || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) throw Error('Проверь адрес электронной почты.');
  if (password.length < 12 || password.length > 128) throw Error('Пароль: от 12 до 128 символов.');
  if (nickname !== undefined && !/^[A-Za-z0-9_]{3,16}$/.test(nickname)) throw Error('Ник: 3–16 латинских букв, цифр или знаков _.');
  return email;
}
export class Accounts {
  constructor(request = globalThis.fetch.bind(globalThis)) { this.request = request; this.session = null; this.recovery = null; }
  clear() { this.session = null; this.recovery = null; }
  async send(path, body, token, method = 'POST') {
    let response;
    try {
      response = await this.request(AUTH_URL + '/' + path, {
        method, credentials: 'omit', cache: 'no-store', redirect: 'error',
        headers: { apikey: PUBLIC_KEY, 'Content-Type': 'application/json', ...(token ? { Authorization: 'Bearer ' + token } : {}) },
        ...(body === undefined ? {} : { body: JSON.stringify(body) }), signal: AbortSignal.timeout(30000)
      });
    } catch (_) { throw Error('Сервис аккаунтов недоступен. Проверь соединение и попробуй позже.'); }
    if (!response.ok) {
      if (response.status === 401 && token) this.clear();
      // Never render raw responses, tokens, emails or SQL diagnostics in the page.
      let code = ''; try { code = (await response.json()).error_code || ''; } catch (_) {}
      if (response.status === 429) throw Error('Слишком много попыток. Подожди перед повтором.');
      if (code === 'email_not_confirmed') throw Error('Сначала подтверди почту по ссылке из письма.');
      if (code === 'invalid_credentials') throw Error('Неверная почта или пароль.');
      if (response.status === 422 || response.status === 400 || response.status === 409) throw Error('Проверь данные, подтверждение почты и доступность ника.');
      throw Error('Не удалось выполнить запрос. Попробуй позже.');
    }
    if (response.status === 204) return null;
    return response.json();
  }
  async requestRecovery(email) {
    email = validate(email, 'temporary-password');
    await this.send('auth/v1/recover?redirect_to=' + encodeURIComponent(CONFIRM_URL), { email });
  }
  async acceptRecovery(token) {
    this.clear();
    if (!token || token.length > 8192) throw Error('Ссылка восстановления недействительна.');
    // Never trust a user/id/type supplied in the URL: verify the bearer with Auth.
    const user = await this.send('auth/v1/user', undefined, token, 'GET');
    if (!/^[0-9a-f-]{36}$/i.test(user?.id || '')) throw Error('Ссылка восстановления недействительна.');
    this.recovery = token;
  }
  async resetPassword(password) {
    validate('validation@example.org', password);
    if (!this.recovery) throw Error('Открой действующую ссылку из письма.');
    const token = this.recovery;
    try {
      await this.send('auth/v1/user', { password }, token, 'PUT');
      // Invalidate all refresh sessions. Existing short-lived JWTs expire separately.
      try { await this.send('auth/v1/logout?scope=global', {}, token); }
      catch (_) { return false; }
      return true;
    } finally { this.clear(); }
  }
  async signUp(email, password, nickname) {
    email = validate(email, password, nickname);
    // Deliberately do not accept an auto-confirm session; explicit sign-in is required.
    await this.send('auth/v1/signup?redirect_to=' + encodeURIComponent(CONFIRM_URL), { email, password, data: { nickname } });
  }
  async signIn(email, password) {
    this.clear(); email = validate(email, password);
    const data = await this.send('auth/v1/token?grant_type=password', { email, password });
    if (!data.access_token || !/^[0-9a-f-]{36}$/i.test(data.user?.id || '') || !Number.isFinite(data.expires_in) || data.expires_in < 1) throw Error('Неверный ответ сервиса аккаунтов.');
    try {
      const rows = await this.send('rest/v1/profiles?id=eq.' + data.user.id + '&select=id,nickname', undefined, data.access_token, 'GET');
      if (!Array.isArray(rows) || rows.length !== 1 || rows[0].id !== data.user.id || !/^[A-Za-z0-9_]{3,16}$/.test(rows[0].nickname)) throw Error('Профиль не найден. Попробуй позже.');
      this.session = { token: data.access_token, id: data.user.id, nickname: rows[0].nickname, expires: Date.now() + data.expires_in * 1000 };
      return this.session;
    } catch (error) { this.clear(); throw error; }
  }
  async resend(email) {
    email = validate(email, 'temporary-password');
    await this.send('auth/v1/resend?redirect_to=' + encodeURIComponent(CONFIRM_URL), { email, type: 'signup' });
  }
  async signOut() {
    const token = this.session?.token; this.clear();
    if (token) await this.send('auth/v1/logout?scope=local', {}, token);
  }
}
