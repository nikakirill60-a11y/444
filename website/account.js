import {Accounts} from './accounts.js';
const $ = id => document.getElementById(id);
const accounts = new Accounts();
let mode = 'signup', busy = false, expiry;
const status = text => { $('account-status').textContent = text; };
const actions = ['submit-account', 'resend-confirmation', 'forgot-password', 'mode-signup', 'mode-signin', 'sign-out'];
function signedIn(value) {
  $('guest').hidden = value; $('signed-in').hidden = !value;
  $('profile-nickname').textContent = value ? accounts.session.nickname : '';
}
function switchMode(next) {
  if (next !== 'reset') accounts.recovery = null;
  mode = next;
  const signup = mode === 'signup', recover = mode === 'recover', reset = mode === 'reset';
  $('nickname-field').hidden = !signup; $('nickname').disabled = !signup; $('nickname').required = signup;
  $('repeat-field').hidden = !(signup || reset);
  $('repeat-password').disabled = !(signup || reset); $('repeat-password').required = signup || reset;
  $('email').disabled = reset;
  $('password-field').hidden = recover; $('password').disabled = recover; $('password').required = !recover;
  $('password').autocomplete = signup || reset ? 'new-password' : 'current-password';
  $('mode-signup').setAttribute('aria-pressed', String(signup));
  $('mode-signin').setAttribute('aria-pressed', String(mode === 'signin'));
  $('form-title').textContent = {signup:'Создать аккаунт', signin:'Войти в аккаунт', recover:'Восстановление пароля', reset:'Новый пароль'}[mode];
  $('submit-account').textContent = {signup:'Зарегистрироваться →', signin:'Войти →', recover:'Отправить ссылку', reset:'Сохранить пароль'}[mode];
  $('password').value = $('repeat-password').value = ''; status('');
}
async function run(action) {
  if (busy) return; busy = true;
  for (const id of actions) $(id).disabled = true;
  status('Подожди…');
  try { await action(); } catch (error) { if (!accounts.session) signedIn(false); status(error.message); }
  finally {
    busy = false; $('password').value = $('repeat-password').value = '';
    for (const id of actions) $(id).disabled = false;
  }
}
$('mode-signup').onclick = () => { if (!busy) switchMode('signup'); };
$('mode-signin').onclick = () => { if (!busy) switchMode('signin'); };
$('forgot-password').onclick = () => { if (!busy) switchMode('recover'); };
$('account-form').addEventListener('submit', event => {
  event.preventDefault();
  const email = $('email').value, password = $('password').value, nickname = $('nickname').value;
  if ((mode === 'signup' || mode === 'reset') && password !== $('repeat-password').value) { status('Пароли не совпадают.'); return; }
  run(async () => {
    if (mode === 'recover') {
      await accounts.requestRecovery(email);
      status('Если адрес подходит для восстановления, на него придёт письмо со ссылкой. Проверь «Спам». У почтового сервиса есть ограничения отправки; при ограничении попробуй позже.');
    } else if (mode === 'reset') {
      let loggedOut;
      try { loggedOut = await accounts.resetPassword(password); }
      catch (error) { switchMode('recover'); throw error; }
      switchMode('signin');
      status(loggedOut ? 'Пароль изменён. Войди с новым паролем.' : 'Пароль изменён, но завершить остальные сеансы не удалось. Войди с новым паролем.');
    } else if (mode === 'signup') {
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
// Read only recovery credentials, erase the URL before making any request, and
// verify the bearer with Auth. Confirmation links never become page sessions.
async function handleLanding() {
  if (!location.hash && !location.search) return;
  const fragment = new URLSearchParams(location.hash.slice(1)), query = new URLSearchParams(location.search);
  const failed = fragment.has('error') || query.has('error');
  const token = fragment.get('type') === 'recovery' ? fragment.get('access_token') : null;
  history.replaceState(null, '', location.pathname);
  if (token && !failed) {
    await run(async () => {
      await accounts.acceptRecovery(token); switchMode('reset');
      status('Введи новый пароль и повтори его.');
    });
  } else {
    switchMode('signin');
    status(failed ? 'Ссылка недействительна или устарела. Запроси письмо повторно.' : 'Ссылка обработана. Войди с почтой и паролем; если почта не подтверждена, запроси письмо повторно.');
  }
}
handleLanding();
window.addEventListener('pagehide', () => { accounts.clear(); clearTimeout(expiry); $('password').value = $('repeat-password').value = ''; signedIn(false); });
