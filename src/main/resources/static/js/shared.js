import { api, getLang } from './api.js';
import { progress } from './progress.js';
import { createEditor } from './editor.js';

export const app = document.getElementById('app');
export const progressPill = document.getElementById('progress-pill');
export const resetBtn = document.getElementById('reset-progress');

export const state = {
  totalItems: 0,
  chrome: {},
  uiBundle: { cheatsheet: [] },
  patternsCache: null,
  activeEditor: null,
  openProblemKey: null,
};

let renderImpl = async () => {};
export function bindRender(fn) { renderImpl = fn; }
function requestRender() { return renderImpl(); }

const ONBOARD_KEY = 'algoprep.onboarded.v1';
const COMPLEXITY_OPTS = ['O(1)', 'O(log n)', 'O(n)', 'O(n log n)', 'O(n^2)', 'O(2^n)'];
export const TRACK_PRESETS = [
  { id: 'Amazon', match: ['Amazon', 'FAANG'] },
  { id: 'Google', match: ['Google', 'FAANG'] },
  { id: 'Meta', match: ['Meta', 'FAANG', 'Facebook'] },
  { id: 'Microsoft', match: ['Microsoft'] },
  { id: 'Netflix', match: ['Netflix'] },
];
export const MOCK_CHECKS = ['restate', 'pattern', 'complexity', 'edges', 'code'];
const INTERVIEW_CHECKS = ['restate', 'pattern', 'complexity', 'edges', 'example'];
const MORE_HREFS = ['#/quiz', '#/challenge', '#/spring', '#/cheatsheet', '#/metrics', '#/settings', '#/report'];

export function t(key, vars = {}) {
  let value = state.chrome[key] ?? key;
  Object.entries(vars).forEach(([k, v]) => {
    value = value.replaceAll(`{${k}}`, String(v));
  });
  return value;
}

export function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;');
}

export function badge(diff) {
  const d = (diff || '').toLowerCase();
  return `<span class="badge ${d}">${escapeHtml(diff)}</span>`;
}

export function difficultyCounts(problems) {
  const counts = { EASY: 0, MEDIUM: 0, HARD: 0 };
  (problems || []).forEach((pr) => {
    const key = String(pr.difficulty || '').toUpperCase();
    if (key in counts) counts[key] += 1;
  });
  return counts;
}

export function difficultyCountHtml(problemsOrCounts, { compact = false } = {}) {
  const c = problemsOrCounts && typeof problemsOrCounts.EASY === 'number'
    ? problemsOrCounts
    : difficultyCounts(problemsOrCounts);
  if (compact) {
    return `<span class="diff-counts compact" title="${escapeHtml(t('difficulty.breakdown', c))}">
      <span class="badge easy">E${c.EASY}</span>
      <span class="badge medium">M${c.MEDIUM}</span>
      <span class="badge hard">H${c.HARD}</span>
    </span>`;
  }
  return `<span class="diff-counts" title="${escapeHtml(t('difficulty.breakdown', c))}">
    <span class="badge easy">${escapeHtml(t('difficulty.easy'))} ${c.EASY}</span>
    <span class="badge medium">${escapeHtml(t('difficulty.medium'))} ${c.MEDIUM}</span>
    <span class="badge hard">${escapeHtml(t('difficulty.hard'))} ${c.HARD}</span>
  </span>`;
}

export function freqBadge(freq) {
  if (!freq) return '';
  return `<span class="badge freq-${(freq || '').toLowerCase()}">${escapeHtml(freq)}</span>`;
}

export function disposeEditor() {
  if (state.activeEditor) {
    try { state.activeEditor.dispose(); } catch { /* ignore */ }
    state.activeEditor = null;
  }
}

export function leaveOpenProblem() {
  if (state.openProblemKey) {
    progress.leaveProblem(state.openProblemKey);
    state.openProblemKey = null;
  }
  disposeEditor();
}

export function localizeDiagram(patternId) {
  const titleKey = `diagram.title.${patternId}`;
  const captionKey = `diagram.caption.${patternId}`;
  const title = t(titleKey);
  const caption = t(captionKey);
  app.querySelectorAll('.diagram-panel h3').forEach((el) => {
    if (title !== titleKey) el.textContent = title;
  });
  app.querySelectorAll('.diagram-caption').forEach((el) => {
    const id = el.getAttribute('data-diagram-caption') || patternId;
    const key = `diagram.caption.${id}`;
    const val = t(key);
    if (val !== key) el.textContent = val;
    else if (caption !== captionKey) el.textContent = caption;
  });
}

export function normalizeComplexity(value) {
  return String(value || '')
    .toLowerCase()
    .replace(/\s+/g, '')
    .replace(/ο|ｏ/g, 'o')
    .replace(/\*\*/g, '^')
    .replace(/×/g, '*')
    .replace(/log\s*\(?\s*n\s*\)?/g, 'logn');
}

export function complexityMatch(user, expected) {
  const u = normalizeComplexity(user);
  const e = normalizeComplexity(expected);
  if (!u || !e) return false;
  return u.includes(e) || e.includes(u);
}

export function complexitySelectHtml(id, selected = '') {
  return `<select id="${id}">
    <option value="">—</option>
    ${COMPLEXITY_OPTS.map((opt) =>
      `<option value="${escapeHtml(opt)}" ${opt === selected ? 'selected' : ''}>${escapeHtml(opt)}</option>`
    ).join('')}
  </select>`;
}

export function showComplexityPanel(problem) {
  let host = document.getElementById('complexity-panel');
  if (!host) {
    host = document.createElement('div');
    host.id = 'complexity-panel';
    const actions = app.querySelector('.actions');
    if (actions) actions.after(host);
    else app.appendChild(host);
  }
  host.className = 'panel complexity-panel';
  host.innerHTML = `
    <h3>${escapeHtml(t('complexity.title'))}</h3>
    <p>${escapeHtml(t('complexity.lede'))}</p>
    <div class="complexity-fields">
      <label>${escapeHtml(t('complexity.time'))}${complexitySelectHtml('cx-time')}</label>
      <label>${escapeHtml(t('complexity.space'))}${complexitySelectHtml('cx-space')}</label>
    </div>
    <div class="actions">
      <button class="btn btn-primary" id="cx-check" type="button">${escapeHtml(t('complexity.check'))}</button>
      <button class="btn btn-ghost" id="cx-close" type="button">${escapeHtml(t('complexity.close'))}</button>
    </div>
    <div id="cx-result"></div>`;
  document.getElementById('cx-close').onclick = () => { host.innerHTML = ''; host.className = 'hidden'; };
  document.getElementById('cx-check').onclick = () => {
    const time = document.getElementById('cx-time').value;
    const space = document.getElementById('cx-space').value;
    const timeOk = complexityMatch(time, problem.timeComplexity);
    const spaceOk = complexityMatch(space, problem.spaceComplexity);
    const result = document.getElementById('cx-result');
    result.innerHTML = `
      <p class="${timeOk ? 'cx-ok' : 'cx-bad'}">${escapeHtml(t('complexity.time'))}:
        ${timeOk ? escapeHtml(t('complexity.correct')) : escapeHtml(t('complexity.wrong', { expected: problem.timeComplexity }))}</p>
      <p class="${spaceOk ? 'cx-ok' : 'cx-bad'}">${escapeHtml(t('complexity.space'))}:
        ${spaceOk ? escapeHtml(t('complexity.correct')) : escapeHtml(t('complexity.wrong', { expected: problem.spaceComplexity }))}</p>`;
  };
}

export function setNav() {
  const hash = location.hash || '#/';
  document.querySelectorAll('.nav a[data-link], .bottom-tabs a[data-link], .more-sheet-links a[data-link]').forEach((a) => {
    const href = a.getAttribute('href');
    const active = href === '#/' ? hash === '#/' || hash === '#' : hash.startsWith(href);
    a.setAttribute('aria-current', active ? 'page' : 'false');
  });
  const moreWrap = document.querySelector('.nav-more');
  if (moreWrap) {
    const moreActive = MORE_HREFS.some((h) => hash.startsWith(h));
    moreWrap.classList.toggle('has-current', moreActive);
  }
  document.querySelectorAll('[data-i18n]').forEach((el) => {
    el.textContent = t(el.getAttribute('data-i18n'));
  });
  document.querySelectorAll('.lang-switch button').forEach((btn) => {
    btn.setAttribute('aria-pressed', btn.dataset.lang === getLang() ? 'true' : 'false');
  });
  const footer = document.querySelector('.site-footer p');
  if (footer) footer.textContent = t('footer');
  const streakEl = document.getElementById('streak-pill');
  if (streakEl) streakEl.textContent = `${t('streak.label')}: ${progress.streak()}`;
  document.body.classList.remove('print-cheatsheet', 'print-report');
}

export function closeNavMenus() {
  const menu = document.getElementById('nav-more-menu');
  const btn = document.getElementById('nav-more-btn');
  if (menu) menu.hidden = true;
  if (btn) btn.setAttribute('aria-expanded', 'false');
  const sheet = document.getElementById('more-sheet');
  const bottomMore = document.getElementById('bottom-more-btn');
  if (sheet) sheet.hidden = true;
  if (bottomMore) bottomMore.setAttribute('aria-expanded', 'false');
}

export function openMoreSheet() {
  const sheet = document.getElementById('more-sheet');
  const bottomMore = document.getElementById('bottom-more-btn');
  if (sheet) sheet.hidden = false;
  if (bottomMore) bottomMore.setAttribute('aria-expanded', 'true');
  setNav();
}

export function tipForJudgeMessage(message, runtimeErrors, compileErrors) {
  const blob = `${message || ''} ${runtimeErrors || ''} ${compileErrors || ''}`.toLowerCase();
  if (/unsupportedoperation|not implemented|todo|abstractmethod/.test(blob)) return 'judge.tip.unsupported';
  if (/nullpointer|npe|null pointer/.test(blob)) return 'judge.tip.npe';
  if (/timeout|timed out|time limit|took too long/.test(blob)) return 'judge.tip.timeout';
  if (/wrong answer|expected|assert|mismatch|failed/.test(blob) || message) return 'judge.tip.wrong';
  return 'judge.tip.generic';
}

export function renderJudgeFailures(result) {
  const failures = result.failures || [];
  if (!failures.length && !result.runtimeErrors && !result.compileErrors) return '';
  const first = failures[0] || {};
  const tipKey = tipForJudgeMessage(
    first.message || '',
    result.runtimeErrors,
    result.compileErrors,
  );
  const caseName = first.name || t('judge.tip.unknownCase');
  const failRows = failures.map((f) =>
    `<p><strong class="fail-case-name">${escapeHtml(f.name || '')}</strong>: ${escapeHtml(f.message || '')}<br>expected=${escapeHtml(JSON.stringify(f.expected))} actual=${escapeHtml(JSON.stringify(f.actual))}</p>`
  ).join('');
  return `
    <div class="judge-insight">
      <h4>${escapeHtml(t('judge.tip.title'))}</h4>
      <p class="fail-case">${escapeHtml(t('judge.tip.case', { name: caseName }))}</p>
      <p>${escapeHtml(t(tipKey))}</p>
    </div>
    ${failRows}`;
}

export function bindInterviewChecklist(problemKey) {
  const host = document.getElementById('interview-checklist');
  if (!host) return;
  const paint = () => {
    const state = progress.getChecklist(problemKey);
    const pct = progress.checklistPercent(problemKey, INTERVIEW_CHECKS);
    host.innerHTML = `
      <h3>${escapeHtml(t('checklist.title'))}</h3>
      <p class="checklist-pct">${escapeHtml(t('checklist.pct', { n: pct }))}</p>
      <div class="checklist-items">
        ${INTERVIEW_CHECKS.map((id) => `
          <label class="checklist-item">
            <input type="checkbox" data-check="${id}" ${state[id] ? 'checked' : ''}/>
            <span>${escapeHtml(t(`checklist.${id}`))}</span>
          </label>`).join('')}
      </div>`;
    host.querySelectorAll('[data-check]').forEach((input) => {
      input.addEventListener('change', () => {
        progress.setChecklistItem(problemKey, input.dataset.check, input.checked);
        paint();
      });
    });
  };
  paint();
}

export function finishOnboarding() {
  localStorage.setItem(ONBOARD_KEY, '1');
  const el = document.getElementById('onboard-overlay');
  if (el) {
    el.hidden = true;
    el.innerHTML = '';
  }
}

export function showOnboardingIfNeeded() {
  if (localStorage.getItem(ONBOARD_KEY)) return;
  const el = document.getElementById('onboard-overlay');
  if (!el) return;
  if (!el.hidden && el.querySelector('.onboard-card')) return;
  const steps = [
    { title: t('onboard.step1.title'), body: t('onboard.step1.body'), href: '#/patterns' },
    { title: t('onboard.step2.title'), body: t('onboard.step2.body'), href: '#/patterns' },
    { title: t('onboard.step3.title'), body: t('onboard.step3.body'), href: '#/mock' },
  ];
  let i = 0;
  const paint = () => {
    const step = steps[i];
    const last = i === steps.length - 1;
    el.hidden = false;
    el.innerHTML = `
      <div class="onboard-card">
        <div class="onboard-steps">${steps.map((_, idx) => `<span class="${idx <= i ? 'on' : ''}"></span>`).join('')}</div>
        <h2>${escapeHtml(step.title)}</h2>
        <p>${escapeHtml(step.body)}</p>
        <div class="onboard-actions">
          <button type="button" class="btn btn-ghost" id="onboard-skip">${escapeHtml(t('onboard.skip'))}</button>
          <a class="btn btn-ghost" href="${step.href}" data-link id="onboard-try">${escapeHtml(t('onboard.try'))}</a>
          <button type="button" class="btn btn-primary" id="onboard-next">${escapeHtml(last ? t('onboard.finish') : t('onboard.next'))}</button>
        </div>
      </div>`;
    document.getElementById('onboard-skip').onclick = finishOnboarding;
    document.getElementById('onboard-next').onclick = () => {
      if (last) finishOnboarding();
      else { i += 1; paint(); }
    };
    document.getElementById('onboard-try').onclick = () => {
      if (last) finishOnboarding();
    };
  };
  paint();
}

export function updateProgressUi() {
  progressPill.textContent = `${progress.percent(state.totalItems)}%`;
  const streakEl = document.getElementById('streak-pill');
  if (streakEl) streakEl.textContent = `${t('streak.label')}: ${progress.streak()}`;
}

export function toast(msg) {
  const el = document.createElement('div');
  el.className = 'toast';
  el.textContent = msg;
  document.body.appendChild(el);
  setTimeout(() => el.remove(), 2200);
}

export async function loadLocales() {
  state.chrome = await api.chrome();
  state.uiBundle = await api.ui();
}

export async function getPatterns() {
  if (!state.patternsCache) state.patternsCache = await api.patterns();
  return state.patternsCache;
}

export function getCodeValue() {
  if (state.activeEditor) return state.activeEditor.getValue();
  return document.getElementById('code-editor')?.value || '';
}

export function setCodeValue(value) {
  if (state.activeEditor) state.activeEditor.setValue(value);
  else {
    const ta = document.getElementById('code-editor');
    if (ta) ta.value = value;
  }
}

export async function mountPlaygroundEditor(starter) {
  const host = document.getElementById('monaco-host');
  const fallback = document.getElementById('code-editor');
  if (!host) return;
  try {
    state.activeEditor = await createEditor(host, starter);
    host.classList.add('monaco-ready');
    if (fallback) fallback.classList.add('hidden');
  } catch {
    host.classList.add('hidden');
    if (fallback) fallback.classList.remove('hidden');
    state.activeEditor = null;
  }
}

export async function ensureTotals() {
  if (state.totalItems) return;
  const [overview, challenges] = await Promise.all([api.overview(), api.challenges()]);
  const patternProblems = overview.patterns.reduce((n, p) => n + p.problemCount, 0);
  state.totalItems = patternProblems + challenges.length;
}

export function resetProgress() {
  if (confirm(t('common.progressReset'))) {
    progress.reset();
    updateProgressUi();
    toast(t('common.progressCleared'));
    requestRender();
  }
}

