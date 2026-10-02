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

export async function renderChallenges() {
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

export async function renderChallenge(id) {
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
  const challengeHints = c.hints || [];
  let challengeHintCount = 0;
  document.getElementById('btn-hints').onclick = () => {
    if (!challengeHints.length) return;
    challengeHintCount = Math.min(challengeHints.length, challengeHintCount + 1);
    const stageKeys = ['problem.hint.notice', 'problem.hint.invariant', 'problem.hint.move'];
    const items = challengeHints.slice(0, challengeHintCount).map((h, i) => {
      const label = stageKeys[i] ? t(stageKeys[i]) : t('problem.hints');
      return `<li><span class="hint-stage">${escapeHtml(label)}</span> ${escapeHtml(h)}</li>`;
    }).join('');
    const more = challengeHintCount < challengeHints.length ? `<p class="meta-mini">${escapeHtml(t('problem.nextHint'))}</p>` : '';
    extra.innerHTML = `<div class="panel"><h3>${escapeHtml(t('problem.hints'))}</h3><ul class="bullet-list">${items}</ul>${more}</div>`;
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

export async function renderQuiz() {
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
      const accepted = new Set([result.correctPattern, ...(result.alsoAccept || [])]);
      document.querySelectorAll('.quiz-option').forEach((b) => {
        if (accepted.has(b.dataset.opt)) b.classList.add('correct');
        if (b.dataset.opt === selected && !result.correct) b.classList.add('wrong');
      });
      document.getElementById('quiz-submit').classList.add('hidden');
      document.getElementById('quiz-next').classList.remove('hidden');
    };
    document.getElementById('quiz-next').onclick = () => { index += 1; selected = null; locked = false; paint(); };
  };
  paint();
}

export async function renderDaily() {
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

export async function renderMock() {
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

export async function renderReview() {
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

export async function renderTracks(company) {
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
