/** Deterministic daily problem picks: one Easy, one Medium, one Hard for the local day. */
import { localDateKey } from './progress.js';

function hash(str) {
  let h = 2166136261;
  for (let i = 0; i < str.length; i++) {
    h ^= str.charCodeAt(i);
    h = Math.imul(h, 16777619);
  }
  return h >>> 0;
}

function nextSeed(seed) {
  return (Math.imul(seed, 1664525) + 1013904223) >>> 0;
}

export function pickDaily(patterns, date = new Date()) {
  const key = localDateKey(date);
  const pool = [];
  const buckets = { EASY: [], MEDIUM: [], HARD: [] };
  patterns.forEach((p) => {
    (p.problems || []).forEach((pr) => {
      const item = { patternId: p.id, patternTitle: p.title, ...pr };
      const idx = pool.length;
      pool.push(item);
      const difficulty = String(pr.difficulty || '').toUpperCase();
      if (buckets[difficulty]) buckets[difficulty].push(idx);
    });
  });
  if (!pool.length) return [];

  const used = new Set();
  const picks = [];
  ['EASY', 'MEDIUM', 'HARD'].forEach((difficulty) => {
    const bucket = buckets[difficulty];
    if (!bucket.length) return;
    let seed = hash(`${key}:algoprep:${difficulty}`);
    for (let attempt = 0; attempt < bucket.length; attempt += 1) {
      seed = nextSeed(seed);
      const idx = bucket[seed % bucket.length];
      if (!used.has(idx)) {
        used.add(idx);
        picks.push(pool[idx]);
        return;
      }
    }
  });

  let seed = hash(`${key}:algoprep:fill`);
  while (picks.length < 3 && used.size < pool.length) {
    seed = nextSeed(seed);
    const idx = seed % pool.length;
    if (used.has(idx)) continue;
    used.add(idx);
    picks.push(pool[idx]);
  }
  return picks;
}
