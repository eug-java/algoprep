import assert from 'node:assert/strict';
import test from 'node:test';
import { complexityClass, complexityMatch } from '../../main/resources/static/js/complexity.js';
import { pickDaily } from '../../main/resources/static/js/daily.js';
import { dueAtLocalDay, localDateKey, progress } from '../../main/resources/static/js/progress.js';

const mem = new Map();
globalThis.localStorage = {
  getItem: (key) => (mem.has(key) ? mem.get(key) : null),
  setItem: (key, value) => mem.set(key, String(value)),
  removeItem: (key) => mem.delete(key),
};

function resetStore() {
  mem.clear();
}

test('complexity classes do not treat a shorter bound as a match', () => {
  assert.equal(complexityMatch('O(n)', 'O(n^2)'), false);
  assert.equal(complexityMatch('O(n log n)', 'O(n)'), false);
  assert.equal(complexityMatch('O(n)', 'O(n log n)'), false);
  assert.equal(complexityMatch('O(n^2)', 'O(mn)'), false);
  assert.equal(complexityMatch('O(n)', 'O(V+E)'), false);
  assert.equal(complexityMatch('O(1)', 'O(n)'), false);
});

test('complexity classes accept equivalent notation', () => {
  assert.equal(complexityMatch('O(n^2)', 'O(n²)'), true);
  assert.equal(complexityMatch('O(n^3)', 'O(n³)'), true);
  assert.equal(complexityMatch('O(mn)', 'O(m*n)'), true);
  assert.equal(complexityMatch('O(mn)', 'O(n*W)'), true);
  assert.equal(complexityMatch('O(n log k)', 'O(k log n)'), true);
  assert.equal(complexityMatch('O(n log k)', 'O(N log k)'), true);
  assert.equal(complexityMatch('O(n log n)', 'O(n log n + m * log n)'), true);
  assert.equal(complexityMatch('O(log n)', 'O(log(min(m,n)))'), true);
  assert.equal(complexityMatch('O(log n)', 'O(log x)'), true);
  assert.equal(complexityMatch('O(1)', 'O(1) excluding output'), true);
  assert.equal(complexityMatch('O(n)', 'O(1) excluding output'), false);
  assert.equal(complexityMatch('O(V+E)', 'O(V+E)'), true);
  assert.equal(complexityMatch('O(n)', 'O(m+n)'), true);
  assert.equal(complexityMatch('O(n)', 'O(|s|+|t|)'), true);
  assert.equal(complexityMatch('O(n)', 'O(h)'), true);
  assert.equal(complexityMatch('O(2^n)', 'O(n * 2^n)'), true);
  assert.equal(complexityMatch('O(2^n)', 'O(4ⁿ/√n)'), true);
  assert.equal(complexityMatch('O(n!)', 'O(n·n!)'), true);
  assert.equal(complexityMatch('O(n^2)', 'O(N * L^2)'), true);
  assert.equal(complexityMatch('O(2^n)', 'O(N * L^2)'), false);
  assert.equal(complexityMatch('O(n^2)', 'O(n^2 * alpha(n))'), true);
  assert.equal(complexityMatch('O(n)', 'O(n * alpha(n))'), true);
  assert.equal(complexityClass('O(n^2)'), 'O(n^2)');
});

test('a catalog string with two bounds accepts either bound alone', () => {
  const median = 'O(log n) insert, O(1) median';
  const rotated = 'O(n) worst, O(log n) average';
  assert.equal(complexityMatch('O(log n)', median), true);
  assert.equal(complexityMatch('O(1)', median), true);
  assert.equal(complexityMatch('O(n)', median), false);
  assert.equal(complexityMatch('O(n)', rotated), true);
  assert.equal(complexityMatch('O(log n)', rotated), true);
  assert.equal(complexityMatch('O(n^2)', rotated), false);
});

test('marking done does not schedule a recall grade', () => {
  resetStore();
  progress.mark('HASH_MAPS:two-sum');
  assert.equal(progress.isDone('HASH_MAPS:two-sum'), true);
  assert.deepEqual(progress.srsDue(), []);
});

test('again is due on the next local day and ease changes later intervals', () => {
  resetStore();
  progress.mark('HASH_MAPS:two-sum');
  const now = new Date(2026, 10, 11, 19, 30, 0);
  const again = progress.srsSchedule('HASH_MAPS:two-sum', 'again', now);
  assert.equal(again.interval, 1);
  assert.equal(again.due, dueAtLocalDay(1, now));
  assert.ok(again.due > now.getTime());
  assert.equal(localDateKey(new Date(again.due)), '2026-11-12');
  assert.deepEqual(progress.srsDue(now.getTime()), []);
  assert.equal(progress.srsDue(again.due).length, 1);

  const first = progress.srsSchedule('HASH_MAPS:two-sum', 'good', now);
  assert.equal(first.interval, 1);
  assert.equal(first.ease, 2.3);
  const second = progress.srsSchedule('HASH_MAPS:two-sum', 'good', now);
  assert.equal(second.interval, 6);
  const third = progress.srsSchedule('HASH_MAPS:two-sum', 'good', now);
  assert.equal(third.interval, Math.round(6 * 2.3));
});

test('streak follows local midnights', () => {
  resetStore();
  const evening = new Date(2026, 10, 11, 23, 30, 0);
  assert.equal(localDateKey(evening), '2026-11-11');
  assert.equal(progress.touchStreak(evening), 1);
  assert.equal(progress.streak(new Date(2026, 10, 11, 23, 59, 0)), 1);
  const afterMidnight = new Date(2026, 10, 12, 0, 5, 0);
  assert.equal(progress.touchStreak(afterMidnight), 2);
  assert.equal(progress.streak(afterMidnight), 2);
  assert.equal(progress.streak(new Date(2026, 10, 14, 12, 0, 0)), 0);
});

test('daily picks one of each difficulty and stays stable for a local day', () => {
  const patterns = [
    {
      id: 'A',
      title: 'A',
      problems: [
        { id: 'e1', difficulty: 'EASY', title: 'e1' },
        { id: 'e2', difficulty: 'EASY', title: 'e2' },
        { id: 'm1', difficulty: 'MEDIUM', title: 'm1' },
        { id: 'h1', difficulty: 'HARD', title: 'h1' },
        { id: 'h2', difficulty: 'HARD', title: 'h2' },
      ],
    },
  ];
  const day = new Date(2026, 10, 11, 22, 0, 0);
  const first = pickDaily(patterns, day);
  const later = pickDaily(patterns, new Date(2026, 10, 11, 23, 30, 0));
  assert.deepEqual(first.map((p) => p.id), later.map((p) => p.id));
  assert.deepEqual(first.map((p) => p.difficulty), ['EASY', 'MEDIUM', 'HARD']);
  const tomorrow = pickDaily(patterns, new Date(2026, 10, 12, 8, 0, 0));
  assert.equal(tomorrow.length, 3);
});
