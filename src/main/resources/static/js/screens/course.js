import { api, getLang } from '../api.js';
import { progress } from '../progress.js';
import { diagramFor } from '../diagrams.js';
import { renderDiff } from '../diff.js';
import { pickDaily } from '../daily.js';
import { tipFor } from '../speak.js';
import { PLANS, planItemKey, planProgress } from '../plans.js';
import {
  app,
  progressPill,
  resetBtn,
  state,
  TRACK_PRESETS,
  MOCK_CHECKS,
  t,
  escapeHtml,
  badge,
  difficultyCounts,
  difficultyCountHtml,
  freqBadge,
  disposeEditor,
  leaveOpenProblem,
  localizeDiagram,
  normalizeComplexity,
  complexityMatch,
  complexitySelectHtml,
  showComplexityPanel,
  setNav,
  closeNavMenus,
  openMoreSheet,
  tipForJudgeMessage,
  renderJudgeFailures,
  bindInterviewChecklist,
  finishOnboarding,
  showOnboardingIfNeeded,
  updateProgressUi,
  toast,
  loadLocales,
  getPatterns,
  getCodeValue,
  setCodeValue,
  mountPlaygroundEditor,
  ensureTotals,
  resetProgress,
} from '../shared.js';

function weekStrip(overview) {
  const weeks = overview.weeks || [];
  if (!weeks.length) return '';
  const titles = Object.fromEntries((overview.patterns || []).map((pattern) => [pattern.id, pattern.title]));
  return `
    <section class="week-strip-wrap">
      <h2>${escapeHtml(t('home.weeks'))}</h2>
      <div class="week-strip">
        ${weeks.map((week) => `
          <article class="week-card">
            <h3>${escapeHtml(t('week.label', { n: week.week }))}</h3>
            <p>${escapeHtml(week.title || '')}</p>
            <div class="week-links">
              ${(week.patternIds || []).map((id) => `<a href="#/patterns/${escapeHtml(id)}" data-link>${escapeHtml(titles[id] || id)}</a>`).join('')}
            </div>
          </article>
        `).join('')}
      </div>
    </section>
  `;
}

export async function renderHome() {
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
    ${weekStrip(overview)}
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

export async function renderPatterns() {
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

export async function renderPattern(id) {
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

export async function renderProblem(patternId, problemId) {
  const [problem, pattern] = await Promise.all([
    api.problem(patternId, problemId),
    api.pattern(patternId),
  ]);
  const key = `${patternId}:${problemId}`;
  state.openProblemKey = key;
  progress.openProblem(key);
  const done = progress.isDone(key);
  const template = await api.judgeTemplate(patternId, problemId);
  const hasJudge = Boolean(template);
  const ascii = problem.walkthroughAscii;
  const showAscii = (problem.difficulty || '').toUpperCase() === 'HARD' && ascii;
  const constraints = problem.constraints || pattern.constraints || '';
  const followUp = problem.followUp || pattern.followUp || '';
  const discussion = Boolean(problem.discussionOnly);

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
      ${problem.example ? `<h3>${escapeHtml(t('problem.example'))}</h3><pre class="ascii-walk">${escapeHtml(problem.example)}</pre>` : ''}
      ${constraints ? `<h3>${escapeHtml(t('problem.constraints'))}</h3><p>${escapeHtml(constraints)}</p>` : ''}
      <div id="hint-box"></div>
      ${followUp ? `<h3>${escapeHtml(t('problem.followUp'))}</h3><p>${escapeHtml(followUp)}</p>` : ''}
      ${problem.failureNote ? `<h3>${escapeHtml(t('problem.failure'))}</h3><p>${escapeHtml(problem.failureNote)}</p>` : ''}
      <p class="meta-mini">${escapeHtml((problem.companies || []).join(' · '))}</p>
      <p class="meta-mini">${escapeHtml(problem.className)}</p>
    </div>
    ${showAscii ? `<div class="panel ascii-panel"><h3>${escapeHtml(t('problem.ascii'))}</h3><pre class="ascii-walk">${escapeHtml(ascii)}</pre></div>` : ''}
    <div class="panel speak-panel">
      <h3>${escapeHtml(t('speak.title'))}</h3>
      <p>${escapeHtml(tipFor(patternId, t))}</p>
    </div>
    <div class="panel checklist-panel" id="interview-checklist"></div>
    ${hasJudge && !discussion ? `
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
      <div class="panel"><p>${escapeHtml(discussion ? t('playground.discussion') : t('playground.unavailable'))}</p></div>`}
    <div class="actions">
      <button class="btn btn-primary" id="btn-source" type="button">${escapeHtml(t('problem.reveal'))}</button>
      <button class="btn btn-ghost" id="btn-diff" type="button">${escapeHtml(t('problem.diff'))}</button>
      <button class="btn btn-signal" id="btn-done" type="button">${escapeHtml(done ? t('problem.undone') : t('problem.done'))}</button>
    </div>
    <div id="complexity-panel" class="hidden"></div>
    <div id="source-panel" class="hidden"></div>
  `;

  bindInterviewChecklist(key);

  const hints = problem.hints || [];
  let hintCount = hints.length ? 1 : 0;
  const stageKeys = ['problem.hint.notice', 'problem.hint.invariant', 'problem.hint.move'];
  const paintHints = () => {
    const box = document.getElementById('hint-box');
    if (!box || !hints.length) return;
    const items = hints.slice(0, hintCount).map((h, i) => {
      const label = stageKeys[i] ? t(stageKeys[i]) : t('problem.hints');
      return `<li><span class="hint-stage">${escapeHtml(label)}</span> ${escapeHtml(h)}</li>`;
    }).join('');
    const more = hintCount < hints.length
      ? `<button class="btn btn-ghost" id="btn-next-hint" type="button">${escapeHtml(t('problem.nextHint'))}</button>`
      : '';
    box.innerHTML = `<h3>${escapeHtml(t('problem.hints'))}</h3><ul class="bullet-list">${items}</ul>${more}`;
    document.getElementById('btn-next-hint')?.addEventListener('click', () => {
      hintCount += 1;
      paintHints();
    });
  };
  paintHints();

  const starter = template?.source || '';
  if (hasJudge && !discussion) mountPlaygroundEditor(starter);

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
    const trap = (pattern.commonMistakes || [])[0] || '';
    const why = [trap, followUp].filter(Boolean).map((line) => `<p>${escapeHtml(line)}</p>`).join('');
    panel.innerHTML = `<div class="panel"><h3>${escapeHtml(src.filePath)}</h3><pre class="code">${escapeHtml(src.source)}</pre>${why ? `<h3>${escapeHtml(t('problem.why'))}</h3>${why}` : ''}</div>`;
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
