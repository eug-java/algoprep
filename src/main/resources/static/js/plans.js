/** Curated week plans — static ids from course catalog. */

export const PLANS = [
  {
    id: 'faang-4w',
    titleKey: 'plans.faang.title',
    ledeKey: 'plans.faang.lede',
    weeks: [
      {
        labelKey: 'week.label',
        week: 1,
        items: [
          { patternId: 'HASH_MAPS', problemId: 'two-sum' },
          { patternId: 'TWO_POINTERS', problemId: 'pair-with-target-sum' },
          { patternId: 'TWO_POINTERS', problemId: 'triplet-sum-zero' },
          { patternId: 'TWO_POINTERS', problemId: 'container-with-most-water' },
          { patternId: 'SLIDING_WINDOW', problemId: 'longest-no-repeat' },
          { patternId: 'SLIDING_WINDOW', problemId: 'min-window-substring' },
          { patternId: 'FAST_SLOW_POINTERS', problemId: 'linked-list-cycle' },
          { patternId: 'IN_PLACE_LINKED_LIST', problemId: 'reverse-linked-list' },
        ],
      },
      {
        labelKey: 'week.label',
        week: 2,
        items: [
          { patternId: 'TREE_BFS', problemId: 'level-order' },
          { patternId: 'TREE_DFS', problemId: 'path-sum' },
          { patternId: 'TREE_DFS', problemId: 'lowest-common-ancestor' },
          { patternId: 'TREE_DFS', problemId: 'max-path-sum' },
          { patternId: 'GRAPHS', problemId: 'number-of-islands' },
          { patternId: 'GRAPHS', problemId: 'clone-graph' },
          { patternId: 'TOPOLOGICAL_SORT', problemId: 'course-schedule' },
          { patternId: 'UNION_FIND', problemId: 'number-of-provinces' },
        ],
      },
      {
        labelKey: 'week.label',
        week: 3,
        items: [
          { patternId: 'MODIFIED_BINARY_SEARCH', problemId: 'search-rotated-array' },
          { patternId: 'MODIFIED_BINARY_SEARCH', problemId: 'find-range' },
          { patternId: 'DYNAMIC_PROGRAMMING', problemId: 'climbing-stairs' },
          { patternId: 'DYNAMIC_PROGRAMMING', problemId: 'house-robber' },
          { patternId: 'DYNAMIC_PROGRAMMING', problemId: 'coin-change' },
          { patternId: 'DYNAMIC_PROGRAMMING', problemId: 'edit-distance' },
          { patternId: 'BACKTRACKING', problemId: 'combination-sum' },
          { patternId: 'BACKTRACKING', problemId: 'word-search' },
        ],
      },
      {
        labelKey: 'week.label',
        week: 4,
        items: [
          { patternId: 'INTERVALS', problemId: 'merge-intervals' },
          { patternId: 'INTERVALS', problemId: 'minimum-meeting-rooms' },
          { patternId: 'TOP_K_ELEMENTS', problemId: 'top-k-frequent' },
          { patternId: 'TOP_K_ELEMENTS', problemId: 'kth-largest' },
          { patternId: 'K_WAY_MERGE', problemId: 'merge-k-sorted-lists' },
          { patternId: 'TWO_HEAPS', problemId: 'find-median-stream' },
          { patternId: 'CUSTOM_DATA_STRUCTURES', problemId: 'lru-cache' },
          { patternId: 'STACKS', problemId: 'daily-temperatures' },
        ],
      },
    ],
  },
  {
    id: 'spring-week',
    titleKey: 'plans.spring.title',
    ledeKey: 'plans.spring.lede',
    weeks: [
      {
        labelKey: 'plans.spring.week',
        week: 1,
        items: [
          { patternId: 'SPRING_BOOT_INTERVIEW', problemId: 'rate-limiter' },
          { patternId: 'SPRING_BOOT_INTERVIEW', problemId: 'idempotency-store' },
          { patternId: 'SPRING_BOOT_INTERVIEW', problemId: 'transactional-outbox' },
          { patternId: 'SPRING_BOOT_INTERVIEW', problemId: 'circuit-breaker' },
          { patternId: 'HASH_MAPS', problemId: 'two-sum' },
          { patternId: 'HASH_MAPS', problemId: 'subarray-sum-equals-k' },
          { patternId: 'CUSTOM_DATA_STRUCTURES', problemId: 'lru-cache' },
          { patternId: 'KNOWING_WHAT_TO_TRACK', problemId: 'majority-element' },
        ],
      },
    ],
  },
];

export function planItemKey(item) {
  return `${item.patternId}:${item.problemId}`;
}

export function planProgress(plan, isDone) {
  const items = plan.weeks.flatMap((w) => w.items);
  const done = items.filter((it) => isDone(planItemKey(it))).length;
  return { done, total: items.length, pct: items.length ? Math.round((done / items.length) * 100) : 0 };
}
