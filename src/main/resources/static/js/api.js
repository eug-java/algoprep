const BASE = '/api/v1/course';
const JUDGE = '/api/v1/judge';

let currentLang = localStorage.getItem('algoprep.lang') || 'en';

export function getLang() {
  return currentLang;
}

export function setLang(lang) {
  currentLang = lang;
  localStorage.setItem('algoprep.lang', lang);
}

async function getJson(path) {
  const sep = path.includes('?') ? '&' : '?';
  const res = await fetch(`${BASE}${path}${sep}lang=${encodeURIComponent(currentLang)}`);
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `HTTP ${res.status}`);
  }
  return res.json();
}

export const api = {
  overview: () => getJson(''),
  ui: () => getJson('/ui'),
  patterns: () => getJson('/patterns'),
  pattern: (id) => getJson(`/patterns/${id}`),
  problem: (patternId, problemId) => getJson(`/patterns/${patternId}/problems/${problemId}`),
  problemSource: (patternId, problemId) => getJson(`/patterns/${patternId}/problems/${problemId}/source`),
  challenges: () => getJson('/challenges'),
  challenge: (id) => getJson(`/challenges/${id}`),
  revealChallenge: (id) => getJson(`/challenges/${id}/reveal`),
  challengeSource: (id) => getJson(`/challenges/${id}/source`),
  quiz: () => getJson('/quiz'),
  checkQuiz: async (id, selectedPattern) => {
    const res = await fetch(
      `${BASE}/quiz/${encodeURIComponent(id)}/check?lang=${encodeURIComponent(currentLang)}`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ selectedPattern }),
      }
    );
    if (!res.ok) throw new Error(await res.text());
    return res.json();
  },
  judgeTemplate: async (patternId, problemId) => {
    const res = await fetch(
      `${JUDGE}/template/${encodeURIComponent(patternId)}/${encodeURIComponent(problemId)}?lang=${encodeURIComponent(currentLang)}`
    );
    if (!res.ok) return null;
    return res.json();
  },
  judge: async (patternId, problemId, source) => {
    const res = await fetch(JUDGE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ patternId, problemId, source, lang: currentLang }),
    });
    const body = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(body.runtimeErrors || body.compileErrors || JSON.stringify(body));
    return body;
  },
  chrome: async () => {
    const res = await fetch(`/i18n/${currentLang}.json`);
    if (!res.ok) throw new Error(`Missing UI locale ${currentLang}`);
    return res.json();
  },
  walkthrough: async (problemId) => {
    const res = await fetch(`${BASE}/walkthroughs/${encodeURIComponent(problemId)}?lang=${encodeURIComponent(currentLang)}`);
    if (!res.ok) return null;
    return res.json();
  },
  createSyncKey: async () => {
    const res = await fetch('/api/v1/sync/keys', { method: 'POST' });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
  },
  pullSync: async (syncKey) => {
    const res = await fetch(`/api/v1/sync/${encodeURIComponent(syncKey)}`);
    if (!res.ok) throw new Error(await res.text());
    return res.json();
  },
  pushSync: async (syncKey, payload) => {
    const res = await fetch(`/api/v1/sync/${encodeURIComponent(syncKey)}`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
  },
  trackMetric: (event) => {
    fetch('/api/v1/metrics/events', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(event),
    }).catch(() => {});
  },
  metricsSummary: async () => {
    const res = await fetch('/api/v1/metrics/summary');
    if (!res.ok) return null;
    return res.json();
  },
};
