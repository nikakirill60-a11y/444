'use strict';
// Static cards remain readable without JS. No network requests, tokens or storage.
const tablist = document.querySelector('[data-wiki-tabs]');
const tabs = Array.from(tablist.querySelectorAll('a'));
const panels = tabs.map(tab => document.getElementById(tab.hash.slice(1)));
const known = new Map(tabs.map((tab, index) => [tab.hash, index]));
const cards = Array.from(document.querySelectorAll('[data-entry]'));
const byId = new Map(cards.map(card => ['#' + card.id, card]));
const search = document.querySelector('#wiki-search');
const group = document.querySelector('#wiki-group');
const rarity = document.querySelector('#wiki-rarity');
const status = document.querySelector('#wiki-results');
const more = document.querySelector('.catalog-more');
const empty = document.querySelector('.catalog-no-results');
const reset = document.querySelector('.catalog-reset');
const pageSize = 24;
let selected = 0;
let limit = pageSize;
let matched = [];
const normalize = value => value.toLocaleLowerCase('ru').replaceAll('ё', 'е').replaceAll('×', 'x').trim();
const searchable = new Map(cards.map(card => [card, normalize(card.querySelector('h3').textContent + ' ' + card.dataset.entry + ' ' + card.querySelector('.catalog-type').textContent)]));

tablist.setAttribute('role', 'tablist');
tabs.forEach((tab, index) => {
  tab.setAttribute('role', 'tab');
  tab.setAttribute('aria-controls', panels[index].id);
  panels[index].setAttribute('role', 'tabpanel');
  panels[index].setAttribute('aria-labelledby', tab.id);
  panels[index].tabIndex = 0;
});
document.querySelector('.catalog-tools').hidden = false;

function updateGroups() {
  group.replaceChildren(new Option('Все подкатегории', ''));
  const groups = [...new Set(Array.from(panels[selected].querySelectorAll('[data-entry]')).map(card => card.dataset.group))].sort((a, b) => a.localeCompare(b, 'ru'));
  groups.forEach(label => group.add(new Option(label, label)));
}

function filter() {
  const query = normalize(search.value);
  matched = Array.from(panels[selected].querySelectorAll('[data-entry]')).filter(card => (!group.value || card.dataset.group === group.value) && (!rarity.value || card.dataset.rarity === rarity.value) && searchable.get(card).includes(query));
  const showing = new Set(matched.slice(0, limit));
  cards.forEach(card => { card.hidden = !showing.has(card); });
  const count = Math.min(limit, matched.length);
  status.textContent = `Найдено: ${matched.length} · показано: ${count}`;
  more.hidden = matched.length <= limit;
  more.textContent = `Показать ещё ${Math.min(pageSize, Math.max(0, matched.length - limit))}`;
  empty.hidden = matched.length !== 0;
}

function activate(index, {focus = false, remember = false} = {}) {
  if (selected !== index) { selected = index; limit = pageSize; updateGroups(); }
  tabs.forEach((tab, current) => {
    const active = current === index;
    tab.setAttribute('aria-selected', String(active));
    tab.tabIndex = active ? 0 : -1;
    panels[current].hidden = !active;
  });
  if (remember && location.hash !== tabs[index].hash) history.pushState(null, '', tabs[index].hash);
  filter();
  if (focus) tabs[index].focus();
}

function restoreSection() {
  // Hashes are looked up in a Map, never inserted as HTML or a CSS selector.
  const card = byId.get(location.hash);
  if (card) {
    search.value = '';
    activate(panels.indexOf(card.closest('.wiki-panel')));
    group.value = ''; rarity.value = '';
    const sectionCards = Array.from(panels[selected].querySelectorAll('[data-entry]'));
    limit = Math.max(pageSize, sectionCards.indexOf(card) + 1);
    filter();
    card.querySelector('details').open = true;
    requestAnimationFrame(() => card.scrollIntoView({block: 'start'}));
  } else activate(known.get(location.hash) ?? 0);
}

tabs.forEach((tab, index) => {
  tab.addEventListener('click', event => {
    if (event.button !== 0 || event.ctrlKey || event.metaKey || event.shiftKey || event.altKey) return;
    event.preventDefault();
    activate(index, {remember: true});
  });
  tab.addEventListener('keydown', event => {
    let next;
    if (event.key === 'ArrowRight') next = (index + 1) % tabs.length;
    else if (event.key === 'ArrowLeft') next = (index + tabs.length - 1) % tabs.length;
    else if (event.key === 'Home') next = 0;
    else if (event.key === 'End') next = tabs.length - 1;
    else if (event.key === ' ') next = index;
    else return;
    event.preventDefault();
    activate(next, {focus: true, remember: true});
  });
});
search.addEventListener('input', () => { limit = pageSize; filter(); });
group.addEventListener('change', () => { limit = pageSize; filter(); });
rarity.addEventListener('change', () => { limit = pageSize; filter(); });
reset.addEventListener('click', () => {
  search.value = ''; group.value = ''; rarity.value = ''; limit = pageSize; filter(); search.focus();
});
more.addEventListener('click', () => {
  const firstNew = matched[limit];
  limit += pageSize; filter();
  // Keep keyboard focus in the newly revealed content, not on a disappearing button.
  if (firstNew) firstNew.querySelector('summary').focus();
});
window.addEventListener('hashchange', restoreSection);
window.addEventListener('popstate', restoreSection);
updateGroups();
restoreSection();
