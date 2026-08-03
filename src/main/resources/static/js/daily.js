/** Deterministic daily problem picks from catalog patterns. */
function hash(str) {
  let h = 2166136261;
  for (let i = 0; i < str.length; i++) {
    h ^= str.charCodeAt(i);
    h = Math.imul(h, 16777619);
  }
  return h >>> 0;
}

export function pickDaily(patterns, date = new Date()) {
  const key = date.toISOString().slice(0, 10);
  const pool = [];
  patterns.forEach((p) => {
    (p.problems || []).forEach((pr) => {
      pool.push({ patternId: p.id, patternTitle: p.title, ...pr });
    });
  });
  if (!pool.length) return [];
  const seed = hash(key + ':algoprep');
  const picks = [];
  const used = new Set();
  let s = seed;
  while (picks.length < 3 && used.size < pool.length) {
    s = (s * 1664525 + 1013904223) >>> 0;
    const idx = s % pool.length;
    if (used.has(idx)) continue;
    used.add(idx);
    picks.push(pool[idx]);
  }
  // prefer mix of difficulties if possible
  return picks;
}
