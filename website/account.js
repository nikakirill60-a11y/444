import {Accounts, validateSkin} from './accounts.js?v=up08';
import {mountPublisher} from './publisher.js?v=up08';
const $ = id => document.getElementById(id);
const accounts = new Accounts();
let mode = 'signup', busy = false, expiry, inboxTimer, selectedSkin = null, selectedModel = 'steve', admin = false;
const status = text => { $('account-status').textContent = text; };
const actions = ['submit-account', 'resend-confirmation', 'forgot-password', 'mode-signup', 'mode-signin', 'sign-out', 'reload-profile', 'profile-recovery', 'skin-file', 'skin-model', 'upload-skin', 'request-dev', 'refresh-inbox'];
const publisher=mountPublisher(accounts,{run,busy:()=>busy});
function signedIn(value) {
  $('guest').hidden = value; $('signed-in').hidden = !value;
  $('account-intro').hidden = value; $('main').classList.toggle('cabinet-open', value);
  $('profile-nickname').textContent = value ? accounts.session.nickname : '';
  $('profile-email').textContent = value ? accounts.session.email : '';
  if (!value) {
    publisher.reset(); clearInterval(inboxTimer); admin = false; selectedSkin = null;
    $('admin-inbox').hidden = true; $('requests').replaceChildren(); $('request-count').textContent = '';
    $('skin-preview').getContext('2d').clearRect(0,0,64,64); $('skin-file').value = '';
    $('skin-state').textContent = 'Скин пока не загружен.';
    $('dev-status').textContent = 'Войди, чтобы проверить доступ.';
    $('request-dev').disabled = $('upload-skin').disabled = true;
  }
}
function updateButtons() {
  publisher.render();
  $('upload-skin').disabled = busy || !accounts.session || !selectedSkin;
  $('request-dev').disabled = busy || !accounts.session || $('request-dev').dataset.state !== 'none';
  for (const button of $('requests').querySelectorAll('button')) button.disabled = busy;
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
  updateButtons();
  status('Подожди…');
  try { await action(); } catch (error) { if (!accounts.session) signedIn(false); status(error.message); }
  finally {
    busy = false; $('password').value = $('repeat-password').value = '';
    for (const id of actions) $(id).disabled = false;
    updateButtons();
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
      clearTimeout(expiry);
      expiry = setTimeout(() => { accounts.clear(); signedIn(false); status('Сессия истекла. Войди снова.'); }, Math.min(session.expires - Date.now(), 2147483647));
      const ok = await loadCabinet(); if (ok) status('Добро пожаловать в личный кабинет.');
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
const devLabels = {
  none:'Заявка ещё не отправлена.', pending:'Заявка отправлена. Ожидает решения администратора.',
  approved:'Доступ к MinETRO dev подтверждён администратором.', rejected:'Администратор отклонил заявку.'
};
async function loadDev() {
  $('request-dev').dataset.state = 'loading'; updateButtons();
  try {
    const state = await accounts.devStatus();
    if (!(state in devLabels)) throw Error('Неизвестный статус заявки.');
    $('dev-status').textContent = devLabels[state]; $('request-dev').dataset.state = state;
  } catch (error) { $('dev-status').textContent = 'Не удалось проверить доступ. Обнови кабинет.'; throw error; }
  finally { updateButtons(); }
}
async function loadInbox() {
  const rows = await accounts.inbox();
  accounts.requireSession();
  $('requests').replaceChildren();
  const pending = rows.filter(row => row.status === 'pending').length;
  $('request-count').textContent = pending ? '(' + pending + ')' : '';
  if (!rows.length) $('requests').textContent = 'Заявок пока нет.';
  for (const row of rows) {
    const item = document.createElement('article'); item.className = 'request-item';
    const title = document.createElement('p');
    title.textContent = 'Игрок ' + row.nickname + ' запрашивает скачивание MinETRO dev'; item.append(title);
    const meta = document.createElement('p'); meta.className = 'request-meta';
    const date = new Date(row.requested_at);
    meta.textContent = (devLabels[row.status] || 'Неизвестный статус') + ' · ' + (Number.isNaN(date.getTime()) ? '' : date.toLocaleString('ru-RU'));
    item.append(meta);
    if (row.status === 'pending') {
      const buttons = document.createElement('div'); buttons.className = 'request-actions';
      for (const approve of [true, false]) {
        const button = document.createElement('button'); button.type = 'button';
        button.className = approve ? 'button primary' : 'button outline';
        button.textContent = approve ? 'Подтвердить' : 'Отклонить';
        button.onclick = () => run(async () => {
          await accounts.review(row.user_id, approve); await loadDev(); await loadInbox();
          status(approve ? 'Доступ подтверждён.' : 'Заявка отклонена.');
        });
        buttons.append(button);
      }
      item.append(buttons);
    }
    $('requests').append(item);
  }
  updateButtons();
}
async function drawSkin(png, model) {
  const height = validateSkin(png, model), session = accounts.requireSession();
  const bitmap = await createImageBitmap(new Blob([png], {type:'image/png'}));
  try {
    if (accounts.session !== session) throw Error('Войди снова.');
    if (bitmap.width !== 64 || bitmap.height !== height) throw Error('Не удалось прочитать PNG-скин.');
    const ctx = $('skin-preview').getContext('2d'); ctx.clearRect(0,0,64,64); ctx.drawImage(bitmap,0,0);
  } finally { bitmap.close(); }
  return height;
}
async function loadCabinet() {
  let ok = true;
  const profile = await accounts.profile();
  $('profile-nickname').textContent = profile.nickname;
  $('skin-model').value = profile.skin_model || 'steve';
  $('skin-file').value = ''; selectedSkin = null;
  $('skin-preview').getContext('2d').clearRect(0,0,64,64);
  try {
    const png = await accounts.skin();
    if (png) { await drawSkin(png, profile.skin_model || 'steve'); $('skin-state').textContent = 'Сохранённый скин профиля.'; }
    else $('skin-state').textContent = 'Скин пока не загружен.';
  } catch (error) { ok = false; $('skin-state').textContent = error.message; }
  try { await loadDev(); } catch (_) { ok = false; }
  try {
    admin = await accounts.isAdmin(); $('admin-inbox').hidden = !admin;
    if (admin) await loadInbox();
  } catch (_) { admin = false; $('admin-inbox').hidden = true; ok = false; }
  try { await publisher.load(); } catch (_) { publisher.reset(); ok = false; }
  clearInterval(inboxTimer);
  if (admin) inboxTimer = setInterval(() => {
    if (!busy && accounts.session && !document.hidden) run(async () => { await loadInbox(); status('Входящие заявки обновлены.'); });
  }, 60000);
  if (!accounts.session) signedIn(false);
  if (!ok) status('Часть данных кабинета недоступна. Попробуй обновить кабинет.');
  return ok;
}
$('reload-profile').onclick = () => run(async () => { if (await loadCabinet()) status('Кабинет обновлён.'); });
$('profile-recovery').onclick = () => run(async () => {
  await accounts.requestRecovery(accounts.requireSession().email);
  status('Запрос принят. Если адрес подходит для восстановления, придёт письмо со ссылкой смены пароля. Проверь «Спам»; при ограничении отправки попробуй позже.');
});
$('skin-file').addEventListener('change', () => run(async () => {
  selectedSkin = null; updateButtons();
  const file = $('skin-file').files[0]; if (!file) return;
  if (file.size > 262144) throw Error('Скин должен быть не больше 256 КБ.');
  const png = new Uint8Array(await file.arrayBuffer()); selectedModel = $('skin-model').value;
  const height = await drawSkin(png, selectedModel);
  // Re-encode the decoded texture. Never upload source metadata or extra chunks.
  const clean = document.createElement('canvas'); clean.width = 64; clean.height = height;
  clean.getContext('2d').drawImage($('skin-preview'), 0, 0);
  const blob = await new Promise(resolve => clean.toBlob(resolve, 'image/png'));
  if (!blob) throw Error('Не удалось подготовить скин.');
  selectedSkin = new Uint8Array(await blob.arrayBuffer()); validateSkin(selectedSkin, selectedModel);
  $('skin-state').textContent = 'Выбран локально. Нажми «Сохранить скин», чтобы загрузить в профиль.';
}));
$('skin-model').addEventListener('change', () => {
  selectedModel = $('skin-model').value;
  try { if (selectedSkin) validateSkin(selectedSkin, selectedModel); }
  catch (error) { selectedSkin = null; status(error.message); }
  updateButtons();
});
$('upload-skin').onclick = () => run(async () => {
  if (!selectedSkin) throw Error('Сначала выбери PNG-скин.');
  await accounts.uploadSkin(selectedSkin, selectedModel);
  await accounts.profile(); selectedSkin = null;
  $('skin-state').textContent = 'Скин сохранён в профиле сайта и лаунчера.'; status('Скин сохранён.');
});
$('request-dev').onclick = () => run(async () => {
  await accounts.devRequest(); await loadDev(); if (admin) await loadInbox(); status('Заявка отправлена в кабинет администратора.');
});
$('refresh-inbox').onclick = () => run(async () => { await loadInbox(); status('Входящие заявки обновлены.'); });
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
