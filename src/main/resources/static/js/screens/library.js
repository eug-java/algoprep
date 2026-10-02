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

export async function renderSkills() {
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

export async function renderPlans() {
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

export async function renderReport() {
  await ensureTotals();
  const patterns = await getPatterns();
  const stats = progress.byPattern(patterns).sort((a, b) => a.ratio - b.ratio);
  const weak = stats.filter((s) => s.ratio < 0.5).slice(0, 6);
  const week = progress.dailyCompletionsThisWeek();
  const weekDone = week.reduce((n, d) => n + d.completed, 0);
  const weekTotal = week.reduce((n, d) => n + d.total, 0);
  const pct = progress.percent(state.totalItems);

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

export async function renderMetrics() {
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

export async function renderSpring() {
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

export function renderCheatsheet() {
  const rows = (state.uiBundle.cheatsheet || []).map((row) => `
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

export function renderSettings() {
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
