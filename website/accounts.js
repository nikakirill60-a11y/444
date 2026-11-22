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
      const rows = await this.send('rest/v1/profiles?id=eq.' + data.user.id + '&select=id,nickname,skin_path,skin_model', undefined, data.access_token, 'GET');
      if (!Array.isArray(rows) || rows.length !== 1 || rows[0].id !== data.user.id || !/^[A-Za-z0-9_]{3,16}$/.test(rows[0].nickname)) throw Error('Профиль не найден. Попробуй позже.');
      this.session = { token: data.access_token, id: data.user.id, nickname: rows[0].nickname, email: data.user.email || email, profile: rows[0], expires: Date.now() + data.expires_in * 1000 };
      return this.session;
    } catch (error) { this.clear(); throw error; }
  }
  requireSession() {
    if (!this.session || this.session.expires <= Date.now()) { this.clear(); throw Error('Сессия истекла. Войди снова.'); }
    return this.session;
  }
  async profile() {
    const session = this.requireSession();
    const rows = await this.send('rest/v1/profiles?id=eq.' + session.id + '&select=id,nickname,skin_path,skin_model', undefined, session.token, 'GET');
    if (!Array.isArray(rows) || rows.length !== 1 || rows[0].id !== session.id || !/^[A-Za-z0-9_]{3,16}$/.test(rows[0].nickname)) throw Error('Не удалось загрузить профиль.');
    this.requireSession();
    session.profile = rows[0]; session.nickname = rows[0].nickname;
    return rows[0];
  }
  async uploadSkin(png, model) {
    const session = this.requireSession();
    validateSkin(png, model);
    const path = session.id + '/skin.png';
    let response;
    try {
      response = await this.request(AUTH_URL + '/storage/v1/object/skins/' + path, {
        method:'POST', credentials:'omit', cache:'no-store', redirect:'error',
        headers:{apikey:PUBLIC_KEY, Authorization:'Bearer ' + session.token, 'Content-Type':'image/png', 'x-upsert':'true'},
        body:png, signal:AbortSignal.timeout(30000)
      });
    } catch (_) { throw Error('Не удалось загрузить скин. Проверь соединение.'); }
    if (response.status === 401) this.clear();
    if (!response.ok) throw Error('Не удалось сохранить скин. Попробуй войти снова.');
    this.requireSession();
    await this.send('rest/v1/profiles?id=eq.' + session.id, {skin_path:path, skin_model:model}, session.token, 'PATCH');
  }
  async skin() {
    const session = this.requireSession(), path = session.profile?.skin_path;
    if (!path) return null;
    if (path !== session.id + '/skin.png') throw Error('Недопустимый путь скина.');
    let response;
    try {
      response = await this.request(AUTH_URL + '/storage/v1/object/authenticated/skins/' + path, {
        method:'GET', credentials:'omit', cache:'no-store', redirect:'error',
        headers:{apikey:PUBLIC_KEY, Authorization:'Bearer ' + session.token}, signal:AbortSignal.timeout(30000)
      });
    } catch (_) { throw Error('Не удалось загрузить сохранённый скин.'); }
    if (response.status === 401) this.clear();
    if (!response.ok) throw Error('Сохранённый скин недоступен. Попробуй обновить кабинет.');
    // The bucket enforces 256 KiB; also cap streamed data before image decoding.
    if (Number(response.headers.get('Content-Length')) > 262144) throw Error('Скин слишком большой.');
    const reader = response.body.getReader(); let size=0; const chunks=[];
    try {
      while (true) { const {done,value}=await reader.read(); if(done)break;
        size+=value.length; if(size>262144)throw Error('Скин слишком большой.'); chunks.push(value);
      }
    } finally { await reader.cancel(); }
    const png = new Uint8Array(size); let offset=0;
    for(const chunk of chunks){png.set(chunk,offset);offset+=chunk.length;}
    validateSkin(png,session.profile.skin_model || 'steve');
    return png;
  }
  async devStatus() {
    const s=this.requireSession();
    const rows=await this.send('rest/v1/dev_access_requests?select=status&user_id=eq.' + s.id,undefined,s.token,'GET');
    return rows[0]?.status || 'none';
  }
  async rpc(name, body={}) { return this.send('rest/v1/rpc/' + name,body,this.requireSession().token); }
  async devRequest() { return this.rpc('request_dev_access'); }
  async isAdmin() { return (await this.rpc('is_launcher_admin')) === true; }
  async inbox() { return this.rpc('dev_access_inbox'); }
  async review(target, approve) {
    if (!/^[0-9a-f-]{36}$/i.test(target) || typeof approve !== 'boolean') throw Error('Некорректное решение.');
    return this.rpc('review_dev_access',{target,approve});
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

// Validate the signature and dimensions before a browser image decoder runs.
export function validateSkin(png, model='steve') {
  if (!(png instanceof Uint8Array) || png.length < 33 || png.length > 262144 ||
      ![137,80,78,71,13,10,26,10].every((v,i)=>png[i]===v) ||
      ![73,72,68,82].every((v,i)=>png[12+i]===v)) throw Error('Нужен PNG-скин 64×64 или 64×32, до 256 КБ.');
  const view = new DataView(png.buffer,png.byteOffset,png.byteLength), height=view.getUint32(20);
  if(view.getUint32(16)!==64 || ![32,64].includes(height) || !['steve','alex'].includes(model) || (model==='alex' && height!==64))
    throw Error('Steve: PNG 64×64 или 64×32. Alex: PNG 64×64.');
  return height;
}
