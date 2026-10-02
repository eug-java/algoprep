import { getLang, setLang } from './api.js';
import { applyTheme, initTheme } from './theme.js';
import {
  app, state, progressPill, resetBtn,
  escapeHtml, t, setNav, closeNavMenus, openMoreSheet,
  leaveOpenProblem, loadLocales, ensureTotals, updateProgressUi,
  showOnboardingIfNeeded, resetProgress, bindRender,
} from './shared.js';
import { renderHome, renderPatterns, renderPattern, renderProblem } from './screens/course.js';
import {
  renderChallenges, renderChallenge, renderQuiz, renderDaily,
  renderMock, renderReview, renderTracks,
} from './screens/practice.js';
import {
  renderSkills, renderPlans, renderReport, renderMetrics,
  renderSpring, renderCheatsheet, renderSettings,
} from './screens/library.js';

function parseRoute() {
  const raw = (location.hash || '#/').replace(/^#/, '');
  const parts = raw.split('/').filter(Boolean);
  if (parts.length === 0) return { name: 'home' };
  if (parts[0] === 'patterns' && parts.length === 1) return { name: 'patterns' };
  if (parts[0] === 'patterns' && parts.length === 2) return { name: 'pattern', id: parts[1] };
  if (parts[0] === 'patterns' && parts[2] === 'problems' && parts[3]) {
    return { name: 'problem', patternId: parts[1], problemId: parts[3] };
  }
  if (parts[0] === 'challenge' && parts.length === 1) return { name: 'challenges' };
  if (parts[0] === 'challenge' && parts[1]) return { name: 'challenge', id: parts[1] };
  if (parts[0] === 'quiz') return { name: 'quiz' };
  if (parts[0] === 'cheatsheet') return { name: 'cheatsheet' };
  if (parts[0] === 'skills') return { name: 'skills' };
  if (parts[0] === 'daily') return { name: 'daily' };
  if (parts[0] === 'mock') return { name: 'mock' };
  if (parts[0] === 'spring') return { name: 'spring' };
  if (parts[0] === 'settings') return { name: 'settings' };
  if (parts[0] === 'review') return { name: 'review' };
  if (parts[0] === 'tracks') return { name: 'tracks', company: parts[1] || null };
  if (parts[0] === 'metrics') return { name: 'metrics' };
  if (parts[0] === 'plans') return { name: 'plans' };
  if (parts[0] === 'report') return { name: 'report' };
  return { name: 'home' };
}

async function render() {
  leaveOpenProblem();
  closeNavMenus();
  setNav();
  app.innerHTML = `<div class="loading">${escapeHtml(t('common.loading'))}</div>`;
  try {
    await loadLocales();
    setNav();
    state.patternsCache = null;
    const route = parseRoute();
    const map = {
      home: renderHome,
      patterns: renderPatterns,
      pattern: () => renderPattern(route.id),
      problem: () => renderProblem(route.patternId, route.problemId),
      challenges: renderChallenges,
      challenge: () => renderChallenge(route.id),
      quiz: renderQuiz,
      cheatsheet: renderCheatsheet,
      skills: renderSkills,
      daily: renderDaily,
      mock: renderMock,
      spring: renderSpring,
      settings: renderSettings,
      review: renderReview,
      tracks: () => renderTracks(route.company),
      metrics: renderMetrics,
      plans: renderPlans,
      report: renderReport,
    };
    await (map[route.name] || renderHome)();
    updateProgressUi();
    showOnboardingIfNeeded();
  } catch (err) {
    app.innerHTML = `<div class="error">${escapeHtml(t('common.failed'))}: ${escapeHtml(err.message)}</div>`;
  }
}


bindRender(render);

progressPill.addEventListener('click', resetProgress);
resetBtn?.addEventListener('click', resetProgress);

document.querySelectorAll('.lang-switch button').forEach((btn) => {
  btn.addEventListener('click', async () => {
    setLang(btn.dataset.lang);
    state.totalItems = 0;
    state.patternsCache = null;
    await render();
  });
});

document.querySelectorAll('[data-theme-btn]').forEach((btn) => {
  btn.addEventListener('click', () => applyTheme(btn.dataset.themeBtn));
});

document.getElementById('nav-more-btn')?.addEventListener('click', (ev) => {
  ev.stopPropagation();
  const menu = document.getElementById('nav-more-menu');
  const btn = document.getElementById('nav-more-btn');
  if (!menu || !btn) return;
  const open = menu.hidden;
  menu.hidden = !open;
  btn.setAttribute('aria-expanded', open ? 'true' : 'false');
});

document.getElementById('bottom-more-btn')?.addEventListener('click', (ev) => {
  ev.stopPropagation();
  const sheet = document.getElementById('more-sheet');
  if (sheet?.hidden) openMoreSheet();
  else closeNavMenus();
});

document.getElementById('more-sheet-close')?.addEventListener('click', closeNavMenus);
document.getElementById('more-sheet-backdrop')?.addEventListener('click', closeNavMenus);

document.addEventListener('click', (ev) => {
  const more = document.querySelector('.nav-more');
  if (more && !more.contains(ev.target)) {
    const menu = document.getElementById('nav-more-menu');
    const btn = document.getElementById('nav-more-btn');
    if (menu) menu.hidden = true;
    if (btn) btn.setAttribute('aria-expanded', 'false');
  }
});

window.addEventListener('hashchange', render);
window.addEventListener('beforeunload', leaveOpenProblem);
window.addEventListener('afterprint', () => {
  document.body.classList.remove('print-cheatsheet', 'print-report');
});
window.addEventListener('DOMContentLoaded', async () => {
  initTheme();
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('/sw.js').catch(() => {});
  }
  try { await loadLocales(); } catch { /* ignore */ }
  setNav();
  await ensureTotals().catch(() => {});
  updateProgressUi();
  render();
});

