const KEY = 'algoprep.progress.v1';
const META_KEY = 'algoprep.meta.v1';

const SRS_INTERVALS = [0, 1, 3, 7, 21];
const DAY_MS = 24 * 60 * 60 * 1000;

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

function todayKey() {
  return new Date().toISOString().slice(0, 10);
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
    if (state[id]) this.srsSchedule(id, 'good');
    return Boolean(state[id]);
  },
  mark(id) {
    const state = load();
    state[id] = Date.now();
    save(state);
    this.touchStreak();
    this.srsSchedule(id, 'good');
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
  touchStreak() {
    const meta = loadMeta();
    const today = todayKey();
    if (meta.lastActive === today) {
      saveMeta(meta);
      return meta.streak || 1;
    }
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    const yKey = yesterday.toISOString().slice(0, 10);
    meta.streak = meta.lastActive === yKey ? (meta.streak || 0) + 1 : 1;
    meta.lastActive = today;
    saveMeta(meta);
    return meta.streak;
  },
  streak() {
    const meta = loadMeta();
    const today = todayKey();
    if (!meta.lastActive) return 0;
    if (meta.lastActive === today) return meta.streak || 0;
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    if (meta.lastActive === yesterday.toISOString().slice(0, 10)) return meta.streak || 0;
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
  /** SM-2 lite: grade again|hard|good|easy → intervals 0/1/3/7/21 */
  srsSchedule(id, grade) {
    const meta = loadMeta();
    meta.srs = meta.srs || {};
    const card = meta.srs[id] || { interval: 0, ease: 2.5, reps: 0, due: 0 };
    let step = card.reps || 0;

    if (grade === 'again') {
      step = 0;
      card.ease = Math.max(1.3, (card.ease || 2.5) - 0.2);
    } else if (grade === 'hard') {
      step = Math.max(1, Math.min(step, 1));
      card.ease = Math.max(1.3, (card.ease || 2.5) - 0.15);
    } else if (grade === 'good') {
      step = Math.min(SRS_INTERVALS.length - 1, step + 1);
    } else if (grade === 'easy') {
      step = Math.min(SRS_INTERVALS.length - 1, step + 2);
      card.ease = (card.ease || 2.5) + 0.15;
    } else {
      step = Math.min(SRS_INTERVALS.length - 1, step + 1);
    }

    card.reps = step;
    card.interval = SRS_INTERVALS[step] ?? 21;
    card.due = Date.now() + card.interval * DAY_MS;
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
