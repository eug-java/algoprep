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
  'IN_PLACE_LINKED_LIST',
  'TWO_HEAPS',
  'K_WAY_MERGE',
  'SUBSETS',
  'GREEDY',
  'CYCLIC_SORT',
  'TOPOLOGICAL_SORT',
  'SORT_AND_SEARCH',
  'MATRICES',
  'STACKS',
  'TRIE',
  'HASH_MAPS',
  'KNOWING_WHAT_TO_TRACK',
  'UNION_FIND',
  'CUSTOM_DATA_STRUCTURES',
  'BITWISE',
  'MATH_GEOMETRY',
  'SPRING_BOOT_INTERVIEW',
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
  IN_PLACE_LINKED_LIST:
    'I will reverse or reorder links in place with a few pointers, updating next references carefully so no node is lost.',
  TWO_HEAPS:
    'I will balance a max-heap and a min-heap so the median or boundary stays available in logarithmic time per update.',
  K_WAY_MERGE:
    'I will merge K sorted streams with a min-heap of size K, always taking the smallest head and advancing that list.',
  SUBSETS:
    'I will generate subsets or permutations by choosing include-or-skip at each index, or by swapping positions and backtracking.',
  GREEDY:
    'I will make the locally optimal choice at each step after proving that a greedy choice property holds for this problem.',
  CYCLIC_SORT:
    'I will place each number at its correct index in a cycle, then scan for the missing or duplicate value.',
  TOPOLOGICAL_SORT:
    'I will build the graph and indegrees, then Kahn-process zero-indegree nodes until order is complete or a cycle remains.',
  SORT_AND_SEARCH:
    'I will sort or bucket the input to unlock linear or binary search, trading a sort cost for a simpler scan.',
  MATRICES:
    'I will treat the matrix as a graph or use boundary/direction indices, careful with visited cells and edges.',
  STACKS:
    'I will use a stack to track unmatched opens, previous results, or candidates that are no longer useful.',
  TRIE:
    'I will insert and search strings via a prefix tree, sharing edges so prefix queries stay proportional to word length.',
  HASH_MAPS:
    'I will store complements or running counts in a hash map so lookups stay expected O(1) while I scan once.',
  KNOWING_WHAT_TO_TRACK:
    'I will identify the frequency or majority statistic to track, then update counters or Boyer-Moore style votes as I scan.',
  UNION_FIND:
    'I will union connected components with path compression and rank, answering connectivity in nearly constant time.',
  CUSTOM_DATA_STRUCTURES:
    'I will combine hash maps with linked structures so get, put, and eviction each stay amortized constant time.',
  BITWISE:
    'I will use bit masks and XOR properties to pack flags or cancel pairs without extra storage.',
  MATH_GEOMETRY:
    'I will reduce geometry to slopes, gcd-normalized vectors, or modular arithmetic before scanning points.',
  SPRING_BOOT_INTERVIEW:
    'I will name the Spring concern—filter, transaction, concurrency, or idempotency—then sketch the failure modes and thread-safety before coding.',
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
