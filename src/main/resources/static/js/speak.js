/** Public tip keys for i18n: speak.tip.<PATTERN_ID> or speak.tip.DEFAULT */

const KNOWN = new Set([
  'TWO_POINTERS',
  'SLIDING_WINDOW',
  'FAST_SLOW_POINTERS',
  'TREE_DFS',
  'TREE_BFS',
  'DYNAMIC_PROGRAMMING',
  'GRAPHS',
  'BACKTRACKING',
  'INTERVALS',
  'TOP_K_ELEMENTS',
  'MODIFIED_BINARY_SEARCH',
]);

/** English fallbacks when i18n key missing */
const FALLBACK = {
  TWO_POINTERS:
    'I notice the input is sorted or can be sorted. I will place two pointers and move the one that improves the invariant, achieving linear time after sorting.',
  SLIDING_WINDOW:
    'This asks for a contiguous subarray/substring with a constraint. I will expand the right bound, shrink the left when invalid, and track the best window.',
  FAST_SLOW_POINTERS:
    'I will use a slow pointer stepping once and a fast pointer stepping twice to detect a cycle or find the middle in constant space.',
  TREE_DFS:
    'I will recurse on the tree, defining what each call returns, handling the null base case, then combining left and right results.',
  TREE_BFS:
    'I will process nodes level by level with a queue; the queue size at the start of each loop is the width of the current level.',
  DYNAMIC_PROGRAMMING:
    'I will define the state, the recurrence, and base cases, then fill bottom-up to avoid recomputing overlapping subproblems.',
  GRAPHS:
    'I will model this as a graph, choose BFS or DFS, and track visited nodes to explore connectivity or shortest paths.',
  BACKTRACKING:
    'I will build a partial solution, prune when constraints fail, then undo the choice and try the next option.',
  INTERVALS:
    'I will sort intervals by start, then merge or scan overlaps in a single pass while tracking the active end.',
  TOP_K_ELEMENTS:
    'I will keep a heap of size K so each insertion stays logarithmic in K, giving an efficient top-K selection.',
  MODIFIED_BINARY_SEARCH:
    'I will binary search on a rotated or unbounded range, deciding which half is still sorted or still feasible each step.',
  DEFAULT:
    'I will restate the problem, identify the pattern, state time and space complexity, then walk through a small example before coding.',
};

export function tipKey(patternId) {
  return KNOWN.has(patternId) ? `speak.tip.${patternId}` : 'speak.tip.DEFAULT';
}

export function tipFor(patternId, translate) {
  const key = tipKey(patternId);
  if (typeof translate === 'function') {
    const value = translate(key);
    if (value && value !== key) return value;
  }
  return FALLBACK[patternId] || FALLBACK.DEFAULT;
}
