const KEY = 'algoprep.progress.v1';
const META_KEY = 'algoprep.meta.v1';

export function localDateKey(date = new Date()) {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

export function shiftLocalDate(date, days) {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate() + days);
}

/** Due at local midnight `days` days after `now`'s local date. One day means the next local day. */
export function dueAtLocalDay(days, now = new Date()) {
  const when = shiftLocalDate(now, days);
  when.setHours(0, 0, 0, 0);
  return when.getTime();
}

function load() {
  try {
    return JSON.parse(localStorage.getItem(KEY) || '{}');
  } catch {
    return {};
  }
}

function save(state) {
  localStorage.setItem(KEY, JSON.stringify(state));
}

function loadMeta() {
  try {
    return JSON.parse(localStorage.getItem(META_KEY) || '{}');
  } catch {
    return {};
  }
}

function saveMeta(meta) {
  localStorage.setItem(META_KEY, JSON.stringify(meta));
}

function todayKey(now = new Date()) {
  return localDateKey(now);
}

function patternOf(id) {
  if (!id) return 'UNKNOWN';
  if (id.startsWith('quiz:')) return id.slice(5);
  if (id.startsWith('challenge:')) return 'CHALLENGE';
  const colon = id.indexOf(':');
  return colon >= 0 ? id.slice(0, colon) : id;
}

function postMetric(event) {
  try {
    fetch('/api/v1/metrics/events', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ ...event, at: Date.now() }),
      keepalive: true,
    }).catch(() => {});
  } catch {
    /* fire-and-forget */
  }
}

export const progress = {
  isDone(id) {
    return Boolean(load()[id]);
  },
  toggle(id) {
    const state = load();
    if (state[id]) delete state[id];
    else state[id] = Date.now();
    save(state);
    return Boolean(state[id]);
  },
  mark(id) {
    const state = load();
    state[id] = Date.now();
    save(state);
    this.touchStreak();
  },
  unmark(id) {
    const state = load();
    delete state[id];
    save(state);
  },
  all() {
    return load();
  },
  count() {
    return Object.keys(load()).length;
  },
  reset() {
    localStorage.removeItem(KEY);
    localStorage.removeItem(META_KEY);
  },
  percent(total) {
    if (!total) return 0;
    return Math.min(100, Math.round((this.count() / total) * 100));
  },
  byPattern(patterns) {
    // patterns: [{id, problems:[{id}]}]
    return patterns.map((p) => {
      const total = p.problems.length;
      const done = p.problems.filter((pr) => this.isDone(`${p.id}:${pr.id}`)).length;
      return {
        id: p.id,
        title: p.title,
        done,
        total,
        ratio: total ? done / total : 0,
      };
    });
  },
  touchStreak(now = new Date()) {
    const meta = loadMeta();
    const today = todayKey(now);
    if (meta.lastActive === today) {
      saveMeta(meta);
      return meta.streak || 1;
    }
    const yKey = localDateKey(shiftLocalDate(now, -1));
    meta.streak = meta.lastActive === yKey ? (meta.streak || 0) + 1 : 1;
    meta.lastActive = today;
    saveMeta(meta);
    return meta.streak;
  },
  streak(now = new Date()) {
    const meta = loadMeta();
    const today = todayKey(now);
    if (!meta.lastActive) return 0;
    if (meta.lastActive === today) return meta.streak || 0;
    if (meta.lastActive === localDateKey(shiftLocalDate(now, -1))) return meta.streak || 0;
    return 0;
  },
  recordDaily(ids) {
    const meta = loadMeta();
    meta.daily = meta.daily || {};
    meta.daily[todayKey()] = ids;
    saveMeta(meta);
  },
  getDaily() {
    return (loadMeta().daily || {})[todayKey()] || null;
  },
  /**
   * SM-2 style recall grade. Ease changes the interval after the first successful reviews.
   * Again is due on the next local day, not in the same moment.
   */
  srsSchedule(id, grade, now = new Date()) {
    const meta = loadMeta();
    meta.srs = meta.srs || {};
    const card = meta.srs[id] || { interval: 0, ease: 2.5, reps: 0, due: 0 };
    let ease = card.ease || 2.5;
    let interval = card.interval || 0;
    let reps = card.reps || 0;

    if (grade === 'again') {
      reps = 0;
      interval = 1;
      ease = Math.max(1.3, ease - 0.2);
    } else if (grade === 'hard') {
      reps = Math.max(reps, 1);
      interval = Math.max(1, Math.round((interval || 1) * 1.2));
      ease = Math.max(1.3, ease - 0.15);
    } else if (grade === 'easy') {
      interval = reps === 0 ? 4 : Math.max(1, Math.round((interval || 1) * ease * 1.3));
      ease = Math.min(3, ease + 0.15);
      reps += 1;
    } else {
      if (reps <= 0) interval = 1;
      else if (reps === 1) interval = 6;
      else interval = Math.max(1, Math.round((interval || 1) * ease));
      reps += 1;
    }

    card.reps = reps;
    card.interval = interval;
    card.ease = Math.round(ease * 100) / 100;
    card.due = dueAtLocalDay(interval, now);
    card.lastGrade = grade;
    card.pattern = patternOf(id);
    meta.srs[id] = card;
    saveMeta(meta);
    return card;
  },
  srsDue(now = Date.now()) {
    const meta = loadMeta();
    const srs = meta.srs || {};
    const done = load();
    const due = [];

    Object.entries(srs).forEach(([id, card]) => {
      if (!card || card.due == null) return;
      if (card.due > now) return;
      const isQuiz = id.startsWith('quiz:');
      if (!isQuiz && !done[id] && !id.startsWith('challenge:')) return;
      due.push({
        id,
        due: card.due,
        interval: card.interval,
        ease: card.ease,
        reps: card.reps,
        pattern: card.pattern || patternOf(id),
        kind: isQuiz ? 'quiz' : id.startsWith('challenge:') ? 'challenge' : 'problem',
      });
    });

    due.sort((a, b) => a.due - b.due);
    return due;
  },
  recordQuizFail(patternId, questionId) {
    const id = `quiz:${patternId}`;
    const meta = loadMeta();
    meta.srs = meta.srs || {};
    meta.quizFails = meta.quizFails || {};
    meta.quizFails[id] = {
      patternId,
      questionId: questionId || null,
      at: Date.now(),
    };
    saveMeta(meta);
    this.srsSchedule(id, 'again');
  },
  openProblem(id) {
    const meta = loadMeta();
    meta.openSession = { id, openedAt: Date.now() };
    saveMeta(meta);
    postMetric({ type: 'open', id, pattern: patternOf(id) });
  },
  leaveProblem(id) {
    const meta = loadMeta();
    const session = meta.openSession;
    if (!session || session.id !== id) {
      meta.openSession = null;
      saveMeta(meta);
      return;
    }
    const durationMs = Math.max(0, Date.now() - (session.openedAt || Date.now()));
    const abandoned = !this.isDone(id);
    const pattern = patternOf(id);
    meta.abandon = meta.abandon || {};
    const row = meta.abandon[pattern] || { count: 0, openMs: 0, opens: 0 };
    row.opens += 1;
    row.openMs += durationMs;
    if (abandoned) row.count += 1;
    meta.abandon[pattern] = row;
    meta.openSession = null;
    saveMeta(meta);
    postMetric({
      type: abandoned ? 'abandon' : 'leave',
      id,
      pattern,
      durationMs,
    });
  },
  abandonStats() {
    const abandon = loadMeta().abandon || {};
    return Object.entries(abandon)
      .map(([pattern, row]) => ({
        pattern,
        count: row.count || 0,
        openMs: row.openMs || 0,
        opens: row.opens || 0,
      }))
      .sort((a, b) => b.count - a.count || b.openMs - a.openMs);
  },
  getChecklist(problemKey) {
    const meta = loadMeta();
    const map = meta.checklist || {};
    return { ...(map[problemKey] || {}) };
  },
  setChecklistItem(problemKey, itemId, checked) {
    const meta = loadMeta();
    meta.checklist = meta.checklist || {};
    const row = { ...(meta.checklist[problemKey] || {}) };
    if (checked) row[itemId] = true;
    else delete row[itemId];
    meta.checklist[problemKey] = row;
    saveMeta(meta);
    return row;
  },
  checklistPercent(problemKey, itemIds) {
    if (!itemIds?.length) return 0;
    const row = this.getChecklist(problemKey);
    const done = itemIds.filter((id) => row[id]).length;
    return Math.round((done / itemIds.length) * 100);
  },
  dailyCompletionsThisWeek() {
    const meta = loadMeta();
    const daily = meta.daily || {};
    const done = load();
    const days = [];
    for (let i = 6; i >= 0; i -= 1) {
      const key = localDateKey(shiftLocalDate(new Date(), -i));
      const picks = daily[key] || [];
      const completed = picks.filter((p) => done[`${p.patternId}:${p.problemId}`]).length;
      days.push({ date: key, total: picks.length, completed });
    }
    return days;
  },
  exportAll() {
    return {
      version: 1,
      exportedAt: new Date().toISOString(),
      progress: load(),
      meta: loadMeta(),
    };
  },
  importAll(payload) {
    if (!payload || typeof payload !== 'object') throw new Error('Invalid payload');
    if (payload.progress) save(payload.progress);
    if (payload.meta) saveMeta(payload.meta);
  },
};
