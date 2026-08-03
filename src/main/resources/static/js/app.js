import { api, getLang, setLang } from './api.js';
import { progress } from './progress.js';
import { applyTheme, initTheme } from './theme.js';
import { diagramFor } from './diagrams.js';
import { renderDiff } from './diff.js';
import { pickDaily } from './daily.js';
import { tipFor } from './speak.js';
import { createEditor } from './editor.js';
import { PLANS, planItemKey, planProgress } from './plans.js';

const app = document.getElementById('app');
const progressPill = document.getElementById('progress-pill');
const resetBtn = document.getElementById('reset-progress');
let totalItems = 0;
let chrome = {};
let uiBundle = { cheatsheet: [] };
let patternsCache = null;
let activeEditor = null;
let openProblemKey = null;

const ONBOARD_KEY = 'algoprep.onboarded.v1';
const COMPLEXITY_OPTS = ['O(1)', 'O(log n)', 'O(n)', 'O(n log n)', 'O(n^2)', 'O(2^n)'];
const TRACK_PRESETS = [
  { id: 'Amazon', match: ['Amazon', 'FAANG'] },
  { id: 'Google', match: ['Google', 'FAANG'] },
  { id: 'Meta', match: ['Meta', 'FAANG', 'Facebook'] },
  { id: 'Microsoft', match: ['Microsoft'] },
  { id: 'Netflix', match: ['Netflix'] },
];
const MOCK_CHECKS = ['restate', 'pattern', 'complexity', 'edges', 'code'];
const INTERVIEW_CHECKS = ['restate', 'pattern', 'complexity', 'edges', 'example'];
const MORE_HREFS = ['#/quiz', '#/challenge', '#/spring', '#/cheatsheet', '#/metrics', '#/settings', '#/report'];

function t(key, vars = {}) {
  let value = chrome[key] ?? key;
  Object.entries(vars).forEach(([k, v]) => {
    value = value.replaceAll(`{${k}}`, String(v));
  });
  return value;
}

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;');
}

function badge(diff) {
  const d = (diff || '').toLowerCase();
  return `<span class="badge ${d}">${escapeHtml(diff)}</span>`;
}

function difficultyCounts(problems) {
  const counts = { EASY: 0, MEDIUM: 0, HARD: 0 };
  (problems || []).forEach((pr) => {
    const key = String(pr.difficulty || '').toUpperCase();
    if (key in counts) counts[key] += 1;
  });
  return counts;
}

function difficultyCountHtml(problemsOrCounts, { compact = false } = {}) {
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

function freqBadge(freq) {
  if (!freq) return '';
  return `<span class="badge freq-${(freq || '').toLowerCase()}">${escapeHtml(freq)}</span>`;
}

function disposeEditor() {
  if (activeEditor) {
    try { activeEditor.dispose(); } catch { /* ignore */ }
    activeEditor = null;
  }
}

function leaveOpenProblem() {
  if (openProblemKey) {
    progress.leaveProblem(openProblemKey);
    openProblemKey = null;
  }
  disposeEditor();
}

function localizeDiagram(patternId) {
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

function normalizeComplexity(value) {
  return String(value || '')
    .toLowerCase()
    .replace(/\s+/g, '')
    .replace(/ο|ｏ/g, 'o')
    .replace(/\*\*/g, '^')
    .replace(/×/g, '*')
    .replace(/log\s*\(?\s*n\s*\)?/g, 'logn');
}

function complexityMatch(user, expected) {
  const u = normalizeComplexity(user);
  const e = normalizeComplexity(expected);
  if (!u || !e) return false;
  return u.includes(e) || e.includes(u);
}

function complexitySelectHtml(id, selected = '') {
  return `<select id="${id}">
    <option value="">—</option>
    ${COMPLEXITY_OPTS.map((opt) =>
      `<option value="${escapeHtml(opt)}" ${opt === selected ? 'selected' : ''}>${escapeHtml(opt)}</option>`
    ).join('')}
  </select>`;
}

function showComplexityPanel(problem) {
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

function setNav() {
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

function closeNavMenus() {
  const menu = document.getElementById('nav-more-menu');
  const btn = document.getElementById('nav-more-btn');
  if (menu) menu.hidden = true;
  if (btn) btn.setAttribute('aria-expanded', 'false');
  const sheet = document.getElementById('more-sheet');
  const bottomMore = document.getElementById('bottom-more-btn');
  if (sheet) sheet.hidden = true;
  if (bottomMore) bottomMore.setAttribute('aria-expanded', 'false');
}

function openMoreSheet() {
  const sheet = document.getElementById('more-sheet');
  const bottomMore = document.getElementById('bottom-more-btn');
  if (sheet) sheet.hidden = false;
  if (bottomMore) bottomMore.setAttribute('aria-expanded', 'true');
  setNav();
}

function tipForJudgeMessage(message, runtimeErrors, compileErrors) {
  const blob = `${message || ''} ${runtimeErrors || ''} ${compileErrors || ''}`.toLowerCase();
  if (/unsupportedoperation|not implemented|todo|abstractmethod/.test(blob)) return 'judge.tip.unsupported';
  if (/nullpointer|npe|null pointer/.test(blob)) return 'judge.tip.npe';
  if (/timeout|timed out|time limit|took too long/.test(blob)) return 'judge.tip.timeout';
  if (/wrong answer|expected|assert|mismatch|failed/.test(blob) || message) return 'judge.tip.wrong';
  return 'judge.tip.generic';
}

function renderJudgeFailures(result) {
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

function bindInterviewChecklist(problemKey) {
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

function finishOnboarding() {
  localStorage.setItem(ONBOARD_KEY, '1');
  const el = document.getElementById('onboard-overlay');
  if (el) {
    el.hidden = true;
    el.innerHTML = '';
  }
}

function showOnboardingIfNeeded() {
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

function updateProgressUi() {
  progressPill.textContent = `${progress.percent(totalItems)}%`;
  const streakEl = document.getElementById('streak-pill');
  if (streakEl) streakEl.textContent = `${t('streak.label')}: ${progress.streak()}`;
}

function toast(msg) {
  const el = document.createElement('div');
  el.className = 'toast';
  el.textContent = msg;
  document.body.appendChild(el);
  setTimeout(() => el.remove(), 2200);
}

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

async function loadLocales() {
  chrome = await api.chrome();
  uiBundle = await api.ui();
}

async function getPatterns() {
  if (!patternsCache) patternsCache = await api.patterns();
  return patternsCache;
}

function getCodeValue() {
  if (activeEditor) return activeEditor.getValue();
  return document.getElementById('code-editor')?.value || '';
}

function setCodeValue(value) {
  if (activeEditor) activeEditor.setValue(value);
  else {
    const ta = document.getElementById('code-editor');
    if (ta) ta.value = value;
  }
}

async function mountPlaygroundEditor(starter) {
  const host = document.getElementById('monaco-host');
  const fallback = document.getElementById('code-editor');
  if (!host) return;
  try {
    activeEditor = await createEditor(host, starter);
    host.classList.add('monaco-ready');
    if (fallback) fallback.classList.add('hidden');
  } catch {
    host.classList.add('hidden');
    if (fallback) fallback.classList.remove('hidden');
    activeEditor = null;
  }
}

async function render() {
  leaveOpenProblem();
  closeNavMenus();
  setNav();
  app.innerHTML = `<div class="loading">${escapeHtml(t('common.loading'))}</div>`;
  try {
    await loadLocales();
    setNav();
    patternsCache = null;
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

async function ensureTotals() {
  if (totalItems) return;
  const [overview, challenges] = await Promise.all([api.overview(), api.challenges()]);
  const patternProblems = overview.patterns.reduce((n, p) => n + p.problemCount, 0);
  totalItems = patternProblems + challenges.length;
}

async function renderHome() {
  await ensureTotals();
  const overview = await api.overview();
  app.innerHTML = `
    <section class="hero">
      <div class="hero-plane" aria-hidden="true">
        <svg viewBox="0 0 1440 900" preserveAspectRatio="xMidYMid slice">
          <defs>
            <linearGradient id="g" x1="0" y1="1" x2="1" y2="0">
              <stop offset="0%" stop-color="#F0B429" stop-opacity="0.95"/>
              <stop offset="100%" stop-color="#7Bed9F" stop-opacity="0.25"/>
            </linearGradient>
          </defs>
          <g fill="none" stroke="rgba(247,250,252,0.10)" stroke-width="1">
            ${Array.from({ length: 30 }, (_, i) => `<line x1="${i * 56}" y1="0" x2="${i * 56}" y2="900"/>`).join('')}
          </g>
          <path class="path-draw" d="M80 780 C260 620, 300 520, 420 460 S700 400, 820 300 S1100 180, 1320 120"
            stroke="url(#g)" stroke-width="5" fill="none" stroke-linecap="round"/>
        </svg>
      </div>
      <div class="hero-copy">
        <p class="hero-brand">AlgoPrep</p>
        <h1>${escapeHtml(t('home.headline'))}</h1>
        <p class="lede">${escapeHtml(t('home.stats', { patterns: overview.patternCount, problems: overview.problemCount }))}</p>
        <p class="lede-diff">${difficultyCountHtml({
          EASY: overview.easyCount || 0,
          MEDIUM: overview.mediumCount || 0,
          HARD: overview.hardCount || 0,
        })}</p>
        <div class="cta-row">
          <a class="btn btn-primary" href="#/patterns" data-link>${escapeHtml(t('home.cta.patterns'))}</a>
          <a class="btn btn-signal" href="#/daily" data-link>${escapeHtml(t('home.cta.daily'))}</a>
          <a class="btn btn-ghost" href="#/mock" data-link>${escapeHtml(t('home.cta.mock'))}</a>
        </div>
      </div>
    </section>
    <div class="home-grid">
      <a class="home-tile" href="#/plans" data-link><h3>${escapeHtml(t('nav.plans'))}</h3><p>${escapeHtml(t('plans.lede'))}</p></a>
      <a class="home-tile" href="#/skills" data-link><h3>${escapeHtml(t('nav.skills'))}</h3><p>${escapeHtml(t('skills.lede'))}</p></a>
      <a class="home-tile" href="#/tracks" data-link><h3>${escapeHtml(t('nav.tracks'))}</h3><p>${escapeHtml(t('tracks.lede'))}</p></a>
      <a class="home-tile" href="#/review" data-link><h3>${escapeHtml(t('nav.review'))}</h3><p>${escapeHtml(t('review.lede'))}</p></a>
      <a class="home-tile" href="#/spring" data-link><h3>${escapeHtml(t('nav.spring'))}</h3><p>${escapeHtml(t('spring.lede'))}</p></a>
      <a class="home-tile" href="#/quiz" data-link><h3>${escapeHtml(t('nav.quiz'))}</h3><p>${escapeHtml(t('quiz.lede'))}</p></a>
      <a class="home-tile" href="#/settings" data-link><h3>${escapeHtml(t('nav.settings'))}</h3><p>${escapeHtml(t('settings.lede'))}</p></a>
      <a class="home-tile" href="#/report" data-link><h3>${escapeHtml(t('nav.report'))}</h3><p>${escapeHtml(t('report.lede'))}</p></a>
    </div>
  `;
}

async function renderPatterns() {
  await ensureTotals();
  const patterns = await getPatterns();
  const companies = [...new Set(patterns.flatMap((p) => p.problems.flatMap((pr) => pr.companies || [])))].sort();

  app.innerHTML = `
    <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
    <div class="section-head">
      <div>
        <h1>${escapeHtml(t('patterns.title'))}</h1>
        <p>${escapeHtml(t('patterns.lede'))}</p>
      </div>
      <div class="meta-row">
        <span>${escapeHtml(t('patterns.modules', { n: patterns.length }))}</span>
        ${difficultyCountHtml(difficultyCounts(patterns.flatMap((p) => p.problems || [])))}
      </div>
    </div>
    <div class="filters panel">
      <label>${escapeHtml(t('filters.company'))}
        <select id="f-company"><option value="">${escapeHtml(t('filters.any'))}</option>
          ${companies.map((c) => `<option value="${escapeHtml(c)}">${escapeHtml(c)}</option>`).join('')}
        </select>
      </label>
      <label>${escapeHtml(t('filters.frequency'))}
        <select id="f-freq"><option value="">${escapeHtml(t('filters.any'))}</option>
          <option value="HIGH">HIGH</option><option value="MEDIUM">MEDIUM</option><option value="LOW">LOW</option>
        </select>
      </label>
      <label>${escapeHtml(t('filters.difficulty'))}
        <select id="f-diff"><option value="">${escapeHtml(t('filters.any'))}</option>
          <option value="EASY">EASY</option><option value="MEDIUM">MEDIUM</option><option value="HARD">HARD</option>
        </select>
      </label>
    </div>
    <div class="pattern-list" id="pattern-list"></div>
  `;

  const paint = () => {
    const company = document.getElementById('f-company').value;
    const freq = document.getElementById('f-freq').value;
    const diff = document.getElementById('f-diff').value;
    const filtered = patterns.map((p) => {
      const problems = p.problems.filter((pr) => {
        if (company && !(pr.companies || []).includes(company)) return false;
        if (freq && pr.frequency !== freq) return false;
        if (diff && pr.difficulty !== diff) return false;
        return true;
      });
      return { ...p, problems, _match: problems.length };
    }).filter((p) => !company && !freq && !diff ? true : p._match > 0);

    document.getElementById('pattern-list').innerHTML = filtered.map((p) => `
      <a class="pattern-row" href="#/patterns/${p.id}" data-link>
        <span class="idx">${String(p.order).padStart(2, '0')}</span>
        <div>
          <h3>${escapeHtml(p.title)}</h3>
          <p class="sub">${escapeHtml(p.subtitle)}</p>
          ${difficultyCountHtml(p.problems, { compact: true })}
        </div>
        <span class="count">${escapeHtml(t('patterns.problems', { n: (company||freq||diff) ? p._match : p.problems.length }))}</span>
      </a>`).join('');
  };
  ['f-company', 'f-freq', 'f-diff'].forEach((id) => {
    document.getElementById(id).onchange = paint;
  });
  paint();
}

async function renderPattern(id) {
  const p = await api.pattern(id);
  const diagram = diagramFor(id);
  app.innerHTML = `
    <a class="back-link" href="#/patterns" data-link>${escapeHtml(t('pattern.back'))}</a>
    <div class="section-head">
      <div>
        <h1>${escapeHtml(p.title)}</h1>
        <p>${escapeHtml(p.subtitle)}</p>
      </div>
      <div class="meta-row">
        <span>#${p.order}</span>
        <span>${escapeHtml(t('patterns.problems', { n: p.problems.length }))}</span>
        ${difficultyCountHtml(p.problems)}
      </div>
    </div>
    ${diagram}
    <div class="panel">
      <h2>${escapeHtml(t('pattern.intuition'))}</h2>
      <p>${escapeHtml(p.intuition)}</p>
      <h3>${escapeHtml(t('pattern.when'))}</h3>
      <p>${escapeHtml(p.recognition)}</p>
    </div>
    ${p.walkthrough ? `<div class="panel"><h2>${escapeHtml(t('pattern.walkthrough'))}</h2><p>${escapeHtml(p.walkthrough)}</p></div>` : ''}
    <div class="panel speak-panel">
      <h3>${escapeHtml(t('speak.title'))}</h3>
      <p id="speak-text">${escapeHtml(tipFor(id, t))}</p>
      <button class="btn btn-ghost" id="btn-speak" type="button">${escapeHtml(t('speak.play'))}</button>
    </div>
    <div class="panel">
      <h3>${escapeHtml(t('pattern.signs'))}</h3>
      <ul class="bullet-list">${(p.telltaleSigns || []).map((s) => `<li>${escapeHtml(s)}</li>`).join('')}</ul>
      <h3 style="margin-top:1rem">${escapeHtml(t('pattern.template'))}</h3>
      <ol class="bullet-list">${(p.templateSteps || []).map((s) => `<li>${escapeHtml(s)}</li>`).join('')}</ol>
    </div>
    <div class="panel two-col">
      <div>
        <h3>${escapeHtml(t('pattern.mistakes'))}</h3>
        <ul class="bullet-list">${(p.commonMistakes || []).map((s) => `<li>${escapeHtml(s)}</li>`).join('')}</ul>
      </div>
      <div>
        <h3>${escapeHtml(t('pattern.avoid'))}</h3>
        <ul class="bullet-list">${(p.whenNotToUse || []).map((s) => `<li>${escapeHtml(s)}</li>`).join('')}</ul>
      </div>
    </div>
    <div class="section-head" style="margin-top:2rem"><div><h2>${escapeHtml(t('pattern.problems'))}</h2></div></div>
    <div class="problem-list">
      ${[...p.problems]
        .sort((a, b) => ['EASY', 'MEDIUM', 'HARD'].indexOf(a.difficulty) - ['EASY', 'MEDIUM', 'HARD'].indexOf(b.difficulty))
        .map((pr) => {
          const key = `${p.id}:${pr.id}`;
          return `<a class="problem-item" href="#/patterns/${p.id}/problems/${pr.id}" data-link>
            <div>
              <h3>${escapeHtml(pr.title)} ${badge(pr.difficulty)} ${freqBadge(pr.frequency)}</h3>
              <p>${escapeHtml(pr.summary)}</p>
              <p class="meta-mini">${escapeHtml((pr.companies || []).join(' · '))}</p>
            </div>
            <span class="done-dot ${progress.isDone(key) ? 'on' : ''}"></span>
          </a>`;
        }).join('')}
    </div>
  `;
  if (diagram) localizeDiagram(id);
  document.getElementById('btn-speak').onclick = () => {
    const text = document.getElementById('speak-text').textContent;
    if ('speechSynthesis' in window) {
      const u = new SpeechSynthesisUtterance(text);
      u.lang = getLang() === 'ru' ? 'ru-RU' : getLang() === 'es' ? 'es-ES' : 'en-US';
      speechSynthesis.cancel();
      speechSynthesis.speak(u);
    } else toast(t('speak.unsupported'));
  };
}

async function renderProblem(patternId, problemId) {
  const [problem, pattern] = await Promise.all([
    api.problem(patternId, problemId),
    api.pattern(patternId),
  ]);
  const key = `${patternId}:${problemId}`;
  openProblemKey = key;
  progress.openProblem(key);
  const done = progress.isDone(key);
  const template = await api.judgeTemplate(patternId, problemId);
  const hasJudge = Boolean(template);
  const ascii = problem.walkthroughAscii;
  const showAscii = (problem.difficulty || '').toUpperCase() === 'HARD' && ascii;

  app.innerHTML = `
    <a class="back-link" href="#/patterns/${patternId}" data-link>← ${escapeHtml(pattern.title)}</a>
    <div class="section-head">
      <div>
        <h1>${escapeHtml(problem.title)}</h1>
        <p>${escapeHtml(problem.summary)}</p>
      </div>
      <div class="meta-row">${badge(problem.difficulty)} ${freqBadge(problem.frequency)}
        <span>${escapeHtml(problem.timeComplexity)}</span>
        <span>${escapeHtml(problem.spaceComplexity)}</span>
      </div>
    </div>
    <div class="panel">
      <h3>${escapeHtml(t('problem.when'))}</h3>
      <p>${escapeHtml(problem.whenToUse)}</p>
      ${(problem.hints || []).length ? `<h3>${escapeHtml(t('problem.hints'))}</h3><ul class="bullet-list">${problem.hints.map((h) => `<li>${escapeHtml(h)}</li>`).join('')}</ul>` : ''}
      <p class="meta-mini">${escapeHtml((problem.companies || []).join(' · '))}</p>
      <p class="meta-mini">${escapeHtml(problem.className)}</p>
    </div>
    ${showAscii ? `<div class="panel ascii-panel"><h3>${escapeHtml(t('problem.ascii'))}</h3><pre class="ascii-walk">${escapeHtml(ascii)}</pre></div>` : ''}
    <div class="panel speak-panel">
      <h3>${escapeHtml(t('speak.title'))}</h3>
      <p>${escapeHtml(tipFor(patternId, t))}</p>
    </div>
    <div class="panel checklist-panel" id="interview-checklist"></div>
    ${hasJudge ? `
      <div class="panel playground">
        <div class="playground-head">
          <h2>${escapeHtml(t('playground.title'))}</h2>
          <span class="meta-mini">${escapeHtml(t('playground.hint'))}</span>
        </div>
        <div id="monaco-host" class="monaco-host"></div>
        <textarea id="code-editor" class="code-editor hidden" spellcheck="false">${escapeHtml(template.source)}</textarea>
        <div class="actions">
          <button class="btn btn-primary" id="btn-run" type="button">${escapeHtml(t('playground.run'))}</button>
          <button class="btn btn-ghost" id="btn-reset-code" type="button">${escapeHtml(t('playground.reset'))}</button>
        </div>
        <div id="judge-out"></div>
      </div>` : `
      <div class="panel"><p>${escapeHtml(t('playground.unavailable'))}</p></div>`}
    <div class="actions">
      <button class="btn btn-primary" id="btn-source" type="button">${escapeHtml(t('problem.reveal'))}</button>
      <button class="btn btn-ghost" id="btn-diff" type="button">${escapeHtml(t('problem.diff'))}</button>
      <button class="btn btn-signal" id="btn-done" type="button">${escapeHtml(done ? t('problem.undone') : t('problem.done'))}</button>
    </div>
    <div id="complexity-panel" class="hidden"></div>
    <div id="source-panel" class="hidden"></div>
  `;

  bindInterviewChecklist(key);

  const starter = template?.source || '';
  if (hasJudge) mountPlaygroundEditor(starter);

  document.getElementById('btn-reset-code')?.addEventListener('click', () => {
    setCodeValue(starter);
  });
  document.getElementById('btn-run')?.addEventListener('click', async () => {
    const out = document.getElementById('judge-out');
    out.innerHTML = `<div class="loading">${escapeHtml(t('common.loading'))}</div>`;
    try {
      const result = await api.judge(patternId, problemId, getCodeValue());
      const ok = result.ok || result.passed === result.total;
      out.innerHTML = `
        <div class="panel ${ok ? 'ok-panel' : 'bad-panel'}">
          <h3>${ok ? escapeHtml(t('playground.pass')) : escapeHtml(t('playground.fail'))}</h3>
          <p>${escapeHtml(t('playground.score', { passed: result.passed ?? 0, total: result.total ?? 0 }))}</p>
          ${result.compileErrors ? `<pre class="code">${escapeHtml(result.compileErrors)}</pre>` : ''}
          ${result.runtimeErrors ? `<pre class="code">${escapeHtml(result.runtimeErrors)}</pre>` : ''}
          ${ok ? '' : renderJudgeFailures(result)}
        </div>`;
      if (ok) {
        progress.mark(key);
        updateProgressUi();
        document.getElementById('btn-done').textContent = t('problem.undone');
        toast(t('common.markedDone'));
        showComplexityPanel(problem);
      }
    } catch (e) {
      const tipKey = tipForJudgeMessage(e.message, e.message, '');
      out.innerHTML = `
        <div class="error">${escapeHtml(e.message)}</div>
        <div class="judge-insight">
          <h4>${escapeHtml(t('judge.tip.title'))}</h4>
          <p>${escapeHtml(t(tipKey))}</p>
        </div>`;
    }
  });

  let officialSource = null;
  document.getElementById('btn-source').onclick = async () => {
    const panel = document.getElementById('source-panel');
    panel.classList.remove('hidden');
    panel.innerHTML = `<div class="loading">${escapeHtml(t('common.loading'))}</div>`;
    const src = await api.problemSource(patternId, problemId);
    officialSource = src.source;
    panel.innerHTML = `<div class="panel"><h3>${escapeHtml(src.filePath)}</h3><pre class="code">${escapeHtml(src.source)}</pre></div>`;
  };

  document.getElementById('btn-diff').onclick = async () => {
    if (!officialSource) {
      const src = await api.problemSource(patternId, problemId);
      officialSource = src.source;
    }
    const user = getCodeValue();
    const panel = document.getElementById('source-panel');
    panel.classList.remove('hidden');
    panel.innerHTML = `<div class="panel"><h3>${escapeHtml(t('problem.diff'))}</h3>${renderDiff(user, officialSource)}</div>`;
  };

  document.getElementById('btn-done').onclick = (ev) => {
    const on = progress.toggle(key);
    if (on) progress.touchStreak();
    ev.currentTarget.textContent = on ? t('problem.undone') : t('problem.done');
    updateProgressUi();
    toast(on ? t('common.markedDone') : t('common.markedUndone'));
    if (on) showComplexityPanel(problem);
  };
}

async function renderChallenges() {
  const list = await api.challenges();
  app.innerHTML = `
    <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
    <div class="section-head">
      <div><h1>${escapeHtml(t('challenge.title'))}</h1><p>${escapeHtml(t('challenge.lede'))}</p></div>
      <div class="meta-row">
        <span>${escapeHtml(t('challenge.drills', { n: list.length }))}</span>
        ${difficultyCountHtml({ EASY: 0, MEDIUM: 0, HARD: list.length })}
      </div>
    </div>
    <div class="problem-list">
      ${list.map((c) => `
        <a class="problem-item" href="#/challenge/${c.id}" data-link>
          <div><h3>${escapeHtml(c.title)} ${badge(c.difficulty)}</h3><p>${escapeHtml(c.prompt).slice(0, 140)}…</p></div>
          <span class="done-dot ${progress.isDone('challenge:' + c.id) ? 'on' : ''}"></span>
        </a>`).join('')}
    </div>`;
}

async function renderChallenge(id) {
  const c = await api.challenge(id);
  const key = `challenge:${id}`;
  app.innerHTML = `
    <a class="back-link" href="#/challenge" data-link>${escapeHtml(t('challenge.back'))}</a>
    <div class="section-head">
      <div><h1>${escapeHtml(c.title)}</h1><p>${escapeHtml(t('challenge.hidden'))}</p></div>
      <div class="meta-row">${badge(c.difficulty)}</div>
    </div>
    <div class="panel"><h2>${escapeHtml(t('challenge.prompt'))}</h2><p>${escapeHtml(c.prompt)}</p></div>
    <div class="actions">
      <button class="btn btn-ghost" id="btn-hints" type="button">${escapeHtml(t('challenge.hints'))}</button>
      <button class="btn btn-ghost" id="btn-reveal" type="button">${escapeHtml(t('challenge.reveal'))}</button>
      <button class="btn btn-primary" id="btn-source" type="button">${escapeHtml(t('challenge.solution'))}</button>
      <button class="btn btn-signal" id="btn-done" type="button">${escapeHtml(progress.isDone(key) ? t('problem.undone') : t('problem.done'))}</button>
    </div>
    <div id="extra"></div>`;
  const extra = document.getElementById('extra');
  document.getElementById('btn-hints').onclick = () => {
    extra.innerHTML = `<div class="panel"><h3>${escapeHtml(t('problem.hints'))}</h3><ul class="bullet-list">${(c.hints || []).map((h) => `<li>${escapeHtml(h)}</li>`).join('')}</ul></div>`;
  };
  document.getElementById('btn-reveal').onclick = async () => {
    const full = await api.revealChallenge(id);
    const alts = (full.alternatePatterns || []).filter(Boolean);
    extra.innerHTML = `<div class="panel"><h3>${escapeHtml(t('challenge.pattern'))}</h3>
      <p><strong>${escapeHtml(full.hiddenPattern)}</strong></p>
      ${alts.length ? `<p class="meta-mini">${escapeHtml(t('challenge.alsoValid'))}: ${alts.map(escapeHtml).join(', ')}</p>` : ''}
      <p>${escapeHtml(full.timeComplexity)} · ${escapeHtml(full.spaceComplexity)}</p></div>`;
  };
  document.getElementById('btn-source').onclick = async () => {
    const src = await api.challengeSource(id);
    extra.innerHTML = `<div class="panel"><h3>${escapeHtml(src.filePath)}</h3><pre class="code">${escapeHtml(src.source)}</pre></div>`;
  };
  document.getElementById('btn-done').onclick = (ev) => {
    const on = progress.toggle(key);
    if (on) progress.touchStreak();
    ev.currentTarget.textContent = on ? t('problem.undone') : t('problem.done');
    updateProgressUi();
  };
}

async function renderQuiz() {
  const data = await api.quiz();
  const questions = data.questions || [];
  let index = 0;
  let score = 0;
  let selected = null;
  let locked = false;

  const paint = () => {
    if (!questions.length) {
      app.innerHTML = `<div class="error">No quiz questions</div>`;
      return;
    }
    if (index >= questions.length) {
      app.innerHTML = `
        <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
        <div class="section-head"><div><h1>${escapeHtml(t('quiz.title'))}</h1>
          <p>${escapeHtml(t('quiz.score', { score, total: questions.length }))}</p></div></div>
        <div class="actions"><button class="btn btn-primary" id="quiz-restart" type="button">${escapeHtml(t('quiz.restart'))}</button></div>`;
      document.getElementById('quiz-restart').onclick = () => { index = 0; score = 0; selected = null; locked = false; paint(); };
      return;
    }
    const q = questions[index];
    app.innerHTML = `
      <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
      <div class="section-head">
        <div><h1>${escapeHtml(t('quiz.title'))}</h1><p>${escapeHtml(t('quiz.lede'))}</p></div>
        <div class="meta-row"><span>${index + 1}/${questions.length}</span><span>${escapeHtml(t('quiz.score', { score, total: questions.length }))}</span></div>
      </div>
      <div class="panel"><h2>${escapeHtml(t('challenge.prompt'))}</h2><p>${escapeHtml(q.prompt)}</p></div>
      <div class="quiz-options">${(q.options || []).map((opt) => `<button type="button" class="quiz-option" data-opt="${escapeHtml(opt)}">${escapeHtml(opt)}</button>`).join('')}</div>
      <div class="actions">
        <button class="btn btn-primary" id="quiz-submit" type="button" disabled>${escapeHtml(t('quiz.submit'))}</button>
        <button class="btn btn-ghost hidden" id="quiz-next" type="button">${escapeHtml(t('quiz.next'))}</button>
      </div>
      <div id="quiz-feedback"></div>`;
    document.querySelectorAll('.quiz-option').forEach((btn) => {
      btn.onclick = () => {
        if (locked) return;
        selected = btn.dataset.opt;
        document.querySelectorAll('.quiz-option').forEach((b) => b.classList.toggle('selected', b === btn));
        document.getElementById('quiz-submit').disabled = false;
      };
    });
    document.getElementById('quiz-submit').onclick = async () => {
      if (!selected || locked) return;
      locked = true;
      const result = await api.checkQuiz(q.id, selected);
      if (result.correct) score += 1;
      else if (result.correctPattern) progress.recordQuizFail(result.correctPattern, q.id);
      document.getElementById('quiz-feedback').innerHTML = `
        <div class="panel ${result.correct ? 'ok-panel' : 'bad-panel'}">
          <h3>${escapeHtml(result.correct ? t('quiz.correct') : t('quiz.wrong'))}</h3>
          <p><strong>${escapeHtml(result.correctPattern)}</strong></p>
          <p>${escapeHtml(result.explanation || '')}</p>
        </div>`;
      document.querySelectorAll('.quiz-option').forEach((b) => {
        if (b.dataset.opt === result.correctPattern) b.classList.add('correct');
        if (b.dataset.opt === selected && !result.correct) b.classList.add('wrong');
      });
      document.getElementById('quiz-submit').classList.add('hidden');
      document.getElementById('quiz-next').classList.remove('hidden');
    };
    document.getElementById('quiz-next').onclick = () => { index += 1; selected = null; locked = false; paint(); };
  };
  paint();
}

async function renderSkills() {
  const patterns = await getPatterns();
  const stats = progress.byPattern(patterns).sort((a, b) => a.ratio - b.ratio);
  app.innerHTML = `
    <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
    <div class="section-head">
      <div><h1>${escapeHtml(t('nav.skills'))}</h1><p>${escapeHtml(t('skills.lede'))}</p></div>
      <div class="meta-row">
        <span>${escapeHtml(t('streak.label'))}: ${progress.streak()}</span>
        <a class="btn btn-ghost" href="#/report" data-link>${escapeHtml(t('report.open'))}</a>
      </div>
    </div>
    <div class="heatmap">
      ${stats.map((s) => {
        const pct = Math.round(s.ratio * 100);
        const tone = pct >= 70 ? 'hot' : pct >= 30 ? 'warm' : 'cold';
        return `<a class="heat-cell ${tone}" href="#/patterns/${s.id}" data-link title="${escapeHtml(s.title)}">
          <strong>${escapeHtml(s.title)}</strong>
          <span>${s.done}/${s.total} · ${pct}%</span>
          <i style="width:${pct}%"></i>
        </a>`;
      }).join('')}
    </div>
    <div class="panel">
      <h3>${escapeHtml(t('skills.weak'))}</h3>
      <ul class="bullet-list">
        ${stats.filter((s) => s.ratio < 0.5).slice(0, 8).map((s) => `<li><a href="#/patterns/${s.id}" data-link>${escapeHtml(s.title)}</a> — ${s.done}/${s.total}</li>`).join('') || `<li>${escapeHtml(t('skills.strong'))}</li>`}
      </ul>
    </div>`;
}

async function renderPlans() {
  const patterns = await getPatterns();
  const titleMap = new Map();
  patterns.forEach((p) => {
    p.problems.forEach((pr) => titleMap.set(`${p.id}:${pr.id}`, pr.title));
  });

  app.innerHTML = `
    <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
    <div class="section-head">
      <div><h1>${escapeHtml(t('plans.title'))}</h1><p>${escapeHtml(t('plans.lede'))}</p></div>
    </div>
    ${PLANS.map((plan) => {
      const { done, total, pct } = planProgress(plan, (k) => progress.isDone(k));
      return `
        <div class="panel plan-block" id="plan-${escapeHtml(plan.id)}">
          <h2>${escapeHtml(t(plan.titleKey))}</h2>
          <p>${escapeHtml(t(plan.ledeKey))}</p>
          <p class="meta-mini">${escapeHtml(t('plans.progress', { done, total, pct }))}</p>
          ${plan.weeks.map((week) => `
            <div class="plan-week">
              <h3>${escapeHtml(t(week.labelKey, { n: week.week }))}</h3>
              <div class="problem-list">
                ${week.items.map((item) => {
                  const key = planItemKey(item);
                  const title = titleMap.get(key) || item.problemId;
                  return `<a class="problem-item" href="#/patterns/${item.patternId}/problems/${item.problemId}" data-link>
                    <div>
                      <h3>${escapeHtml(title)}</h3>
                      <p class="meta-mini">${escapeHtml(item.patternId)}</p>
                    </div>
                    <span class="done-dot ${progress.isDone(key) ? 'on' : ''}"></span>
                  </a>`;
                }).join('')}
              </div>
            </div>`).join('')}
        </div>`;
    }).join('')}`;
}

async function renderReport() {
  await ensureTotals();
  const patterns = await getPatterns();
  const stats = progress.byPattern(patterns).sort((a, b) => a.ratio - b.ratio);
  const weak = stats.filter((s) => s.ratio < 0.5).slice(0, 6);
  const week = progress.dailyCompletionsThisWeek();
  const weekDone = week.reduce((n, d) => n + d.completed, 0);
  const weekTotal = week.reduce((n, d) => n + d.total, 0);
  const pct = progress.percent(totalItems);

  app.innerHTML = `
    <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
    <div class="section-head">
      <div><h1>${escapeHtml(t('report.title'))}</h1><p>${escapeHtml(t('report.lede'))}</p></div>
      <div class="actions">
        <button class="btn btn-primary" id="btn-report-print" type="button">${escapeHtml(t('report.print'))}</button>
      </div>
    </div>
    <div class="panel report-print print-area">
      <h1>${escapeHtml(t('report.title'))}</h1>
      <p class="meta-mini">${escapeHtml(new Date().toISOString().slice(0, 10))}</p>
      <div class="report-grid">
        <div class="report-stat"><strong>${progress.streak()}</strong><span>${escapeHtml(t('streak.label'))}</span></div>
        <div class="report-stat"><strong>${pct}%</strong><span>${escapeHtml(t('report.progress'))}</span></div>
        <div class="report-stat"><strong>${weekDone}/${weekTotal}</strong><span>${escapeHtml(t('report.dailyWeek'))}</span></div>
      </div>
      <h3>${escapeHtml(t('report.weak'))}</h3>
      <ul class="bullet-list">
        ${weak.map((s) => `<li>${escapeHtml(s.title)} — ${s.done}/${s.total}</li>`).join('') || `<li>${escapeHtml(t('skills.strong'))}</li>`}
      </ul>
      <h3>${escapeHtml(t('report.dailyDays'))}</h3>
      <ul class="bullet-list">
        ${week.map((d) => `<li>${escapeHtml(d.date)} — ${d.completed}/${d.total}</li>`).join('')}
      </ul>
    </div>`;

  document.getElementById('btn-report-print').onclick = () => {
    document.body.classList.add('print-report');
    document.body.classList.remove('print-cheatsheet');
    window.print();
  };
}

async function renderDaily() {
  const patterns = await getPatterns();
  let picks = progress.getDaily();
  if (!picks) {
    picks = pickDaily(patterns).map((p) => ({ patternId: p.patternId, problemId: p.id, title: p.title, difficulty: p.difficulty }));
    progress.recordDaily(picks);
  }
  app.innerHTML = `
    <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
    <div class="section-head">
      <div><h1>${escapeHtml(t('daily.title'))}</h1><p>${escapeHtml(t('daily.lede'))}</p></div>
      <div class="meta-row"><span>${escapeHtml(t('streak.label'))}: ${progress.streak()}</span></div>
    </div>
    <div class="problem-list">
      ${picks.map((p) => {
        const key = `${p.patternId}:${p.problemId}`;
        return `<a class="problem-item" href="#/patterns/${p.patternId}/problems/${p.problemId}" data-link>
          <div><h3>${escapeHtml(p.title)} ${badge(p.difficulty)}</h3><p>${escapeHtml(p.patternId)}</p></div>
          <span class="done-dot ${progress.isDone(key) ? 'on' : ''}"></span>
        </a>`;
      }).join('')}
    </div>`;
}

function mockChecklistKey(sessionId) {
  return `algoprep.mock.checklist.${sessionId}`;
}

function loadMockChecklist(sessionId) {
  try {
    return JSON.parse(localStorage.getItem(mockChecklistKey(sessionId)) || '{}');
  } catch {
    return {};
  }
}

function saveMockChecklist(sessionId, state) {
  localStorage.setItem(mockChecklistKey(sessionId), JSON.stringify(state));
}

async function renderMock() {
  const [patterns, challenges] = await Promise.all([getPatterns(), api.challenges()]);
  const hardPool = [];
  patterns.forEach((p) => p.problems.filter((pr) => pr.difficulty === 'HARD').forEach((pr) => {
    hardPool.push({
      type: 'problem',
      patternId: p.id,
      id: pr.id,
      title: pr.title,
      summary: pr.summary,
      hint: (pr.hints && pr.hints[0]) || pr.summary,
    });
  }));
  challenges.forEach((c) => hardPool.push({
    type: 'challenge',
    id: c.id,
    title: c.title,
    summary: c.prompt,
    hint: (c.hints && c.hints[0]) || '',
  }));

  let minutes = 35;
  let item = null;
  let timerId = null;
  let left = 0;
  let startedAt = 0;
  let sessionId = '';
  let timeline = [];
  let finished = false;

  const pushEvent = (type) => {
    timeline.push({ type, at: Date.now() });
  };

  const mount = () => {
    app.innerHTML = `
      <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
      <div class="section-head">
        <div><h1>${escapeHtml(t('mock.title'))}</h1><p>${escapeHtml(t('mock.lede'))}</p></div>
      </div>
      <div class="panel">
        <label>${escapeHtml(t('mock.duration'))}
          <select id="mock-min"><option value="30">30</option><option value="35" selected>35</option><option value="45">45</option></select>
        </label>
        <div class="actions" style="margin-top:1rem">
          <button class="btn btn-primary" id="mock-start" type="button">${escapeHtml(t('mock.start'))}</button>
        </div>
      </div>
      <div id="mock-stage"></div>`;
    document.getElementById('mock-start').onclick = () => {
      minutes = Number(document.getElementById('mock-min').value);
      item = hardPool[Math.floor(Math.random() * hardPool.length)];
      left = minutes * 60;
      startedAt = Date.now();
      sessionId = `${Date.now()}-${item.id}`;
      timeline = [];
      finished = false;
      pushEvent('started');
      runSession();
    };
  };

  const runSession = () => {
    const stage = () => {
      document.getElementById('mock-stage').innerHTML = `
        <div class="panel mock-timer"><strong id="clock">${fmt(left)}</strong></div>
        <div class="panel">
          <h2>${escapeHtml(item.title)}</h2>
          <p>${escapeHtml(item.summary)}</p>
          <p class="meta-mini">${escapeHtml(t('mock.nolabel'))}</p>
        </div>
        <div class="actions">
          ${item.hint ? `<button class="btn btn-ghost" id="mock-hint" type="button">${escapeHtml(t('mock.hint'))}</button>` : ''}
          <button class="btn btn-ghost" id="mock-end" type="button">${escapeHtml(t('mock.end'))}</button>
        </div>
        <div id="mock-hint-out"></div>
        <div id="mock-review"></div>`;
      document.getElementById('mock-end').onclick = finish;
      document.getElementById('mock-hint')?.addEventListener('click', () => {
        pushEvent('hintOpened');
        document.getElementById('mock-hint-out').innerHTML =
          `<div class="panel"><p>${escapeHtml(item.hint)}</p></div>`;
      });
    };
    stage();
    clearInterval(timerId);
    timerId = setInterval(() => {
      left -= 1;
      const clock = document.getElementById('clock');
      if (clock) clock.textContent = fmt(Math.max(0, left));
      if (left <= 0) finish();
    }, 1000);
  };

  const finish = async () => {
    if (finished) return;
    finished = true;
    clearInterval(timerId);
    pushEvent('ended');
    const durationSec = Math.max(0, Math.round((Date.now() - startedAt) / 1000));
    const review = document.getElementById('mock-review');
    if (!review) return;

    const checks = loadMockChecklist(sessionId);
    const timelineHtml = timeline.map((ev) => {
      const label = ev.type === 'started'
        ? t('mock.event.started')
        : ev.type === 'hintOpened'
          ? t('mock.event.hint')
          : t('mock.event.ended');
      const ts = new Date(ev.at).toLocaleTimeString();
      return `<li><span>${escapeHtml(label)}</span><time>${escapeHtml(ts)}</time></li>`;
    }).join('');

    const checklistHtml = MOCK_CHECKS.map((id) => `
      <label class="mock-check">
        <input type="checkbox" data-check="${id}" ${checks[id] ? 'checked' : ''}/>
        <span>${escapeHtml(t(`mock.check.${id}`))}</span>
      </label>`).join('');

    let reveal = '';
    if (item.type === 'challenge') {
      const full = await api.revealChallenge(item.id);
      reveal = `<p>${escapeHtml(t('challenge.pattern'))}: <strong>${escapeHtml(full.hiddenPattern)}</strong></p>
        <a class="btn btn-primary" href="#/challenge/${item.id}" data-link>${escapeHtml(t('mock.open'))}</a>`;
    } else {
      reveal = `<p>${escapeHtml(t('challenge.pattern'))}: <strong>${escapeHtml(item.patternId)}</strong></p>
        <a class="btn btn-primary" href="#/patterns/${item.patternId}/problems/${item.id}" data-link>${escapeHtml(t('mock.open'))}</a>`;
    }

    review.innerHTML = `
      <div class="panel ok-panel">
        <h3>${escapeHtml(t('mock.review'))}</h3>
        ${reveal}
        <p class="meta-mini">${escapeHtml(t('mock.durationSec', { n: durationSec }))}</p>
      </div>
      <div class="panel">
        <h3>${escapeHtml(t('mock.timeline'))}</h3>
        <ul class="mock-timeline">${timelineHtml}</ul>
      </div>
      <div class="panel">
        <h3>${escapeHtml(t('mock.checklist'))}</h3>
        <div class="mock-checklist">${checklistHtml}</div>
      </div>`;

    review.querySelectorAll('[data-check]').forEach((input) => {
      input.addEventListener('change', () => {
        const state = loadMockChecklist(sessionId);
        state[input.dataset.check] = input.checked;
        saveMockChecklist(sessionId, state);
      });
    });
  };

  const fmt = (s) => `${String(Math.floor(s / 60)).padStart(2, '0')}:${String(s % 60).padStart(2, '0')}`;
  mount();
}

async function renderReview() {
  const patterns = await getPatterns();
  const titleMap = new Map();
  patterns.forEach((p) => {
    titleMap.set(p.id, p.title);
    p.problems.forEach((pr) => titleMap.set(`${p.id}:${pr.id}`, pr.title));
  });
  const due = progress.srsDue();

  const cardHtml = (card) => {
    let href = '#/patterns';
    let title = card.id;
    let sub = card.pattern || '';
    if (card.kind === 'quiz') {
      href = `#/patterns/${card.pattern}`;
      title = t('review.quizCard');
      sub = titleMap.get(card.pattern) || card.pattern;
    } else if (card.kind === 'challenge') {
      const cid = card.id.replace(/^challenge:/, '');
      href = `#/challenge/${cid}`;
      title = titleMap.get(card.id) || cid;
    } else {
      const [pid, prid] = card.id.split(':');
      href = `#/patterns/${pid}/problems/${prid}`;
      title = titleMap.get(card.id) || prid;
      sub = titleMap.get(pid) || pid;
    }
    return `
      <div class="review-card panel" data-srs-id="${escapeHtml(card.id)}">
        <div>
          <h3>${escapeHtml(title)}</h3>
          <p class="meta-mini">${escapeHtml(sub)}</p>
        </div>
        <a class="btn btn-ghost" href="${href}" data-link>${escapeHtml(t('review.open'))}</a>
        <p class="review-grade-label">${escapeHtml(t('review.grade'))}</p>
        <div class="review-grades">
          <button type="button" class="btn btn-ghost" data-grade="again">${escapeHtml(t('review.again'))}</button>
          <button type="button" class="btn btn-ghost" data-grade="hard">${escapeHtml(t('review.hard'))}</button>
          <button type="button" class="btn btn-primary" data-grade="good">${escapeHtml(t('review.good'))}</button>
          <button type="button" class="btn btn-signal" data-grade="easy">${escapeHtml(t('review.easy'))}</button>
        </div>
      </div>`;
  };

  app.innerHTML = `
    <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
    <div class="section-head">
      <div><h1>${escapeHtml(t('review.title'))}</h1><p>${escapeHtml(t('review.lede'))}</p></div>
      <div class="meta-row"><span>${escapeHtml(t('review.dueCount', { n: due.length }))}</span></div>
    </div>
    <div class="review-list" id="review-list">
      ${due.length ? due.map(cardHtml).join('') : `<div class="panel"><p>${escapeHtml(t('review.empty'))}</p></div>`}
    </div>`;

  document.querySelectorAll('.review-card').forEach((card) => {
    card.querySelectorAll('[data-grade]').forEach((btn) => {
      btn.onclick = () => {
        progress.srsSchedule(card.dataset.srsId, btn.dataset.grade);
        card.remove();
        const left = document.querySelectorAll('.review-card').length;
        const meta = app.querySelector('.meta-row span');
        if (meta) meta.textContent = t('review.dueCount', { n: left });
        if (!left) {
          document.getElementById('review-list').innerHTML =
            `<div class="panel"><p>${escapeHtml(t('review.empty'))}</p></div>`;
        }
      };
    });
  });
}

async function renderTracks(company) {
  const patterns = await getPatterns();
  const active = company || TRACK_PRESETS[0].id;
  const preset = TRACK_PRESETS.find((p) => p.id === active) || TRACK_PRESETS[0];
  const tags = new Set(preset.match.map((s) => s.toLowerCase()));

  const grouped = patterns.map((p) => {
    const problems = p.problems.filter((pr) =>
      (pr.companies || []).some((c) => tags.has(String(c).toLowerCase()))
    );
    return { id: p.id, title: p.title, problems };
  }).filter((g) => g.problems.length);

  const total = grouped.reduce((n, g) => n + g.problems.length, 0);

  app.innerHTML = `
    <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
    <div class="section-head">
      <div><h1>${escapeHtml(t('tracks.title'))}</h1><p>${escapeHtml(t('tracks.lede'))}</p></div>
      <div class="meta-row">
        <span>${escapeHtml(t('tracks.problems', { n: total }))}</span>
        ${difficultyCountHtml(difficultyCounts(grouped.flatMap((g) => g.problems)))}
      </div>
    </div>
    <div class="track-tabs">
      ${TRACK_PRESETS.map((p) =>
        `<a class="track-tab ${p.id === preset.id ? 'active' : ''}" href="#/tracks/${encodeURIComponent(p.id)}" data-link>${escapeHtml(p.id)}</a>`
      ).join('')}
    </div>
    <div class="track-groups">
      ${grouped.length ? grouped.map((g) => `
        <div class="panel track-group">
          <h2><a href="#/patterns/${g.id}" data-link>${escapeHtml(g.title)}</a></h2>
          <div class="problem-list">
            ${g.problems.map((pr) => {
              const key = `${g.id}:${pr.id}`;
              return `<a class="problem-item" href="#/patterns/${g.id}/problems/${pr.id}" data-link>
                <div>
                  <h3>${escapeHtml(pr.title)} ${badge(pr.difficulty)} ${freqBadge(pr.frequency)}</h3>
                  <p class="meta-mini">${escapeHtml((pr.companies || []).join(' · '))}</p>
                </div>
                <span class="done-dot ${progress.isDone(key) ? 'on' : ''}"></span>
              </a>`;
            }).join('')}
          </div>
        </div>`).join('') : `<div class="panel"><p>${escapeHtml(t('tracks.empty'))}</p></div>`}
    </div>`;
}

async function renderMetrics() {
  const patterns = await getPatterns();
  const titles = Object.fromEntries(patterns.map((p) => [p.id, p.title]));
  const stats = progress.abandonStats().filter((s) => s.count > 0);
  const max = Math.max(1, ...stats.map((s) => s.count));
  const server = await api.metricsSummary().catch(() => null);

  app.innerHTML = `
    <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
    <div class="section-head">
      <div><h1>${escapeHtml(t('metrics.title'))}</h1><p>${escapeHtml(t('metrics.lede'))}</p></div>
    </div>
    ${stats.length ? `
      <div class="panel metrics-panel">
        <h3>${escapeHtml(t('metrics.local'))}</h3>
        <div class="metrics-bars">
          ${stats.slice(0, 12).map((s) => {
            const pct = Math.round((s.count / max) * 100);
            const label = titles[s.pattern] || s.pattern;
            return `<div class="metrics-row">
              <a class="metrics-label" href="#/patterns/${encodeURIComponent(s.pattern)}" data-link>${escapeHtml(label)}</a>
              <div class="metrics-bar-track"><span class="metrics-bar" style="width:${pct}%"></span></div>
              <span class="metrics-val">${s.count} ${escapeHtml(t('metrics.abandons'))} · ${s.opens} ${escapeHtml(t('metrics.opens'))}</span>
            </div>`;
          }).join('')}
        </div>
      </div>` : `<div class="panel"><p>${escapeHtml(t('metrics.empty'))}</p></div>`}
    ${server ? `
      <div class="panel">
        <h3>${escapeHtml(t('metrics.server'))}</h3>
        <p class="meta-mini">${escapeHtml(t('metrics.events', { n: server.totalEvents || 0 }))}</p>
        <ul class="bullet-list">
          ${(server.topAbandoned || []).slice(0, 8).map((row) =>
            `<li>${escapeHtml(titles[row.patternId] || row.patternId)} — ${row.abandons}</li>`).join('') || `<li>${escapeHtml(t('metrics.empty'))}</li>`}
        </ul>
      </div>` : ''}
  `;
}

async function renderSpring() {
  const patterns = await getPatterns();
  const spring = patterns.find((p) => p.id === 'SPRING_BOOT_INTERVIEW' || p.track === 'spring');
  if (!spring) {
    app.innerHTML = `<div class="error">Spring track not found</div>`;
    return;
  }
  await renderPattern(spring.id);
  const head = app.querySelector('.section-head');
  if (head) {
    const banner = document.createElement('div');
    banner.className = 'panel';
    banner.innerHTML = `<p>${escapeHtml(t('spring.lede'))}</p>`;
    head.after(banner);
  }
}

function renderCheatsheet() {
  const rows = (uiBundle.cheatsheet || []).map((row) => `
    <div class="cheat-row"><span>${escapeHtml(row.q)}</span><strong>${escapeHtml(row.a)}</strong></div>`).join('');
  app.innerHTML = `
    <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
    <div class="section-head">
      <div><h1>${escapeHtml(t('cheatsheet.title'))}</h1><p>${escapeHtml(t('cheatsheet.lede'))}</p></div>
      <div class="actions"><button class="btn btn-ghost" id="btn-print" type="button">${escapeHtml(t('cheatsheet.exportPdf'))}</button></div>
    </div>
    <div class="panel cheatsheet print-area">${rows}</div>`;
  document.getElementById('btn-print').onclick = () => {
    document.body.classList.add('print-cheatsheet');
    document.body.classList.remove('print-report');
    window.print();
  };
}

function renderSettings() {
  const syncKey = localStorage.getItem('algoprep.syncKey') || '';
  app.innerHTML = `
    <a class="back-link" href="#/" data-link>${escapeHtml(t('common.home'))}</a>
    <div class="section-head"><div><h1>${escapeHtml(t('nav.settings'))}</h1><p>${escapeHtml(t('settings.lede'))}</p></div></div>
    <div class="panel">
      <h3>${escapeHtml(t('report.title'))}</h3>
      <p>${escapeHtml(t('report.lede'))}</p>
      <div class="actions">
        <a class="btn btn-primary" href="#/report" data-link>${escapeHtml(t('report.open'))}</a>
      </div>
    </div>
    <div class="panel">
      <h3>${escapeHtml(t('settings.export'))}</h3>
      <div class="actions">
        <button class="btn btn-primary" id="btn-export" type="button">${escapeHtml(t('settings.export'))}</button>
        <label class="btn btn-ghost">${escapeHtml(t('settings.import'))}
          <input id="import-file" type="file" accept="application/json" hidden />
        </label>
        <button class="btn btn-ghost" id="btn-reset" type="button">${escapeHtml(t('common.reset'))}</button>
      </div>
      <p class="meta-mini">${escapeHtml(t('settings.note'))}</p>
    </div>
    <div class="panel">
      <h3>${escapeHtml(t('settings.sync'))}</h3>
      <p>${escapeHtml(t('settings.sync.lede'))}</p>
      <label class="meta-mini">${escapeHtml(t('settings.sync.key'))}
        <input id="sync-key" type="text" value="${escapeHtml(syncKey)}" style="width:100%;margin-top:0.35rem;padding:0.45rem;font-family:var(--font-mono);font-size:0.85rem" />
      </label>
      <div class="actions" style="margin-top:0.85rem">
        <button class="btn btn-ghost" id="btn-sync-new" type="button">${escapeHtml(t('settings.sync.create'))}</button>
        <button class="btn btn-primary" id="btn-sync-push" type="button">${escapeHtml(t('settings.sync.push'))}</button>
        <button class="btn btn-ghost" id="btn-sync-pull" type="button">${escapeHtml(t('settings.sync.pull'))}</button>
      </div>
    </div>`;
  document.getElementById('btn-export').onclick = () => {
    const blob = new Blob([JSON.stringify(progress.exportAll(), null, 2)], { type: 'application/json' });
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = `algoprep-progress-${new Date().toISOString().slice(0, 10)}.json`;
    a.click();
  };
  document.getElementById('import-file').onchange = async (ev) => {
    const file = ev.target.files?.[0];
    if (!file) return;
    try {
      const text = await file.text();
      progress.importAll(JSON.parse(text));
      toast(t('settings.imported'));
      updateProgressUi();
    } catch (e) {
      toast(e.message);
    }
  };
  document.getElementById('btn-reset').onclick = resetProgress;
  const keyInput = document.getElementById('sync-key');
  document.getElementById('btn-sync-new').onclick = async () => {
    try {
      const { syncKey: key } = await api.createSyncKey();
      keyInput.value = key;
      localStorage.setItem('algoprep.syncKey', key);
      toast(t('settings.sync.created'));
    } catch (e) { toast(e.message); }
  };
  document.getElementById('btn-sync-push').onclick = async () => {
    const key = keyInput.value.trim();
    if (!key) return toast(t('settings.sync.needKey'));
    try {
      const data = progress.exportAll();
      await api.pushSync(key, { version: data.version || 1, progress: data.progress, meta: data.meta, clientId: 'web' });
      localStorage.setItem('algoprep.syncKey', key);
      toast(t('settings.sync.pushed'));
    } catch (e) { toast(e.message); }
  };
  document.getElementById('btn-sync-pull').onclick = async () => {
    const key = keyInput.value.trim();
    if (!key) return toast(t('settings.sync.needKey'));
    try {
      const remote = await api.pullSync(key);
      progress.importAll({ progress: remote.progress || {}, meta: remote.meta || {} });
      localStorage.setItem('algoprep.syncKey', key);
      updateProgressUi();
      toast(t('settings.sync.pulled'));
    } catch (e) { toast(e.message); }
  };
}

function resetProgress() {
  if (confirm(t('common.progressReset'))) {
    progress.reset();
    updateProgressUi();
    toast(t('common.progressCleared'));
    render();
  }
}

progressPill.addEventListener('click', resetProgress);
resetBtn?.addEventListener('click', resetProgress);

document.querySelectorAll('.lang-switch button').forEach((btn) => {
  btn.addEventListener('click', async () => {
    setLang(btn.dataset.lang);
    totalItems = 0;
    patternsCache = null;
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
