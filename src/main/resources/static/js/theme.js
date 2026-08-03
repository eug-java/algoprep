const KEY = 'algoprep.theme.v1';

export function getTheme() {
  return localStorage.getItem(KEY) || 'light';
}

export function applyTheme(theme) {
  const value = theme === 'dark' ? 'dark' : 'light';
  document.documentElement.setAttribute('data-theme', value);
  localStorage.setItem(KEY, value);
  document.querySelectorAll('[data-theme-btn]').forEach((btn) => {
    btn.setAttribute('aria-pressed', btn.dataset.themeBtn === value ? 'true' : 'false');
  });
  return value;
}

export function initTheme() {
  applyTheme(getTheme());
}
