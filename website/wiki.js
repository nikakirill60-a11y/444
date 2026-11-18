'use strict';
// Progressive enhancement: without JS all four anchor sections remain readable.
const tablist = document.querySelector('[data-wiki-tabs]');
const tabs = Array.from(tablist.querySelectorAll('a'));
const panels = tabs.map(tab => document.getElementById(tab.hash.slice(1)));
const known = new Map(tabs.map((tab, index) => [tab.hash, index]));
tablist.setAttribute('role', 'tablist');
tabs.forEach((tab, index) => {
  tab.setAttribute('role', 'tab');
  tab.setAttribute('aria-controls', panels[index].id);
  panels[index].setAttribute('role', 'tabpanel');
  panels[index].setAttribute('aria-labelledby', tab.id);
  panels[index].tabIndex = 0;
});

function activate(index, {focus = false, remember = false} = {}) {
  tabs.forEach((tab, current) => {
    const selected = current === index;
    tab.setAttribute('aria-selected', String(selected));
    tab.tabIndex = selected ? 0 : -1;
    panels[current].hidden = !selected;
  });
  if (remember && location.hash !== tabs[index].hash) {
    history.pushState(null, '', tabs[index].hash);
  }
  if (focus) tabs[index].focus();
}

function restoreSection() {
  // Do not interpret arbitrary URL content as markup or a CSS selector.
  activate(known.get(location.hash) ?? 0);
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
window.addEventListener('hashchange', restoreSection);
window.addEventListener('popstate', restoreSection);
restoreSection();
