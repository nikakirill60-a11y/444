import {Accounts} from './accounts.js';
const $ = id => document.getElementById(id);
const accounts = new Accounts();
let mode = 'signup', busy = false, expiry;
const status = text => { $('account-status').textContent = text; };
function signedIn(value) {
  $('guest').hidden = value; $('signed-in').hidden = !value;
  $('profile-nickname').textContent = value ? accounts.session.nickname : '';
}
function switchMode(next) {
  if (busy) return;
  mode = next; const signup = mode === 'signup';
  for (const id of ['nickname-field', 'repeat-field']) $(id).hidden = !signup;
  for (const id of ['nickname', 'repeat-password']) { $(id).disabled = !signup; $(id).required = signup; }
  $('password').autocomplete = signup ? 'new-password' : 'current-password';
  $('mode-signup').setAttribute('aria-pressed', String(signup));
  $('mode-signin').setAttribute('aria-pressed', String(!signup));
  $('form-title').textContent = signup ? 'Создать аккаунт' : 'Войти в аккаунт';
  $('submit-account').textContent = signup ? 'Зарегистрироваться →' : 'Войти →';
  $('password').value = $('repeat-password').value = ''; status('');
}
async function run(action) {
  if (busy) return; busy = true;
  for (const id of ['submit-account', 'resend-confirmation', 'mode-signup', 'mode-signin', 'sign-out']) $(id).disabled = true;
  status('Подожди…');
  try { await action(); } catch (error) { if (!accounts.session) signedIn(false); status(error.message); }
  finally {
    busy = false; $('password').value = $('repeat-password').value = '';
    for (const id of ['submit-account', 'resend-confirmation', 'mode-signup', 'mode-signin', 'sign-out']) $(id).disabled = false;
  }
}
$('mode-signup').onclick = () => switchMode('signup');
$('mode-signin').onclick = () => switchMode('signin');
$('account-form').addEventListener('submit', event => {
  event.preventDefault();
  const email = $('email').value, password = $('password').value, nickname = $('nickname').value;
  if (mode === 'signup' && password !== $('repeat-password').value) { status('Пароли не совпадают.'); return; }
  run(async () => {
    if (mode === 'signup') {
      await accounts.signUp(email, password, nickname);
      status('Запрос регистрации принят. Если данные подходят, на почту придёт письмо со ссылкой подтверждения. Затем выбери «Вход».');
    } else {
      const session = await accounts.signIn(email, password); signedIn(true);
      status('Ты вошёл в аккаунт MinETRO.'); clearTimeout(expiry);
      expiry = setTimeout(() => { accounts.clear(); signedIn(false); status('Сессия истекла. Войди снова.'); }, Math.min(session.expires - Date.now(), 2147483647));
    }
  });
});
$('resend-confirmation').onclick = () => run(async () => {
  await accounts.resend($('email').value);
  status('Запрос принят. Если аккаунт ожидает подтверждения, сервис отправит письмо. Проверь «Спам»; при ограничении отправки попробуй позже.');
});
$('sign-out').onclick = () => run(async () => {
  clearTimeout(expiry);
  try { await accounts.signOut(); } finally { signedIn(false); }
  status('Ты вышел из аккаунта.');
});
// Supabase itself verifies the email. Never store/use URL bearer tokens on this landing page.
if (location.hash || location.search) {
  const failed = new URLSearchParams(location.hash.slice(1)).has('error') || new URLSearchParams(location.search).has('error');
  history.replaceState(null, '', location.pathname);
  switchMode('signin');
  status(failed ? 'Ссылка недействительна или устарела. Запроси письмо повторно.' : 'Ссылка обработана. Войди с почтой и паролем; если почта не подтверждена, запроси письмо повторно.');
}
window.addEventListener('pagehide', () => { accounts.clear(); clearTimeout(expiry); $('password').value = $('repeat-password').value = ''; signedIn(false); });
