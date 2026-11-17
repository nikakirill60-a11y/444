'use strict';
const toggle = document.querySelector('.menu-toggle');
const navigation = document.querySelector('#navigation');
function closeMenu() {
  navigation.classList.remove('open');
  toggle.setAttribute('aria-expanded', 'false');
  toggle.setAttribute('aria-label', 'Открыть навигацию');
}
toggle.addEventListener('click', () => {
  const open = navigation.classList.toggle('open');
  toggle.setAttribute('aria-expanded', String(open));
  toggle.setAttribute('aria-label', open ? 'Закрыть навигацию' : 'Открыть навигацию');
});
navigation.addEventListener('click', event => {
  if (event.target.closest('a')) closeMenu();
});
document.addEventListener('keydown', event => {
  if (event.key === 'Escape' && navigation.classList.contains('open')) {
    closeMenu();
    toggle.focus();
  }
});
document.querySelector('#copy-server').addEventListener('click', async event => {
  const address = event.currentTarget.dataset.copy;
  const status = document.querySelector('#copy-status');
  try {
    await navigator.clipboard.writeText(address);
    status.textContent = 'Адрес скопирован';
  } catch (_) {
    status.textContent = 'Скопируй вручную: ' + address;
  }
});
