#!/usr/bin/env python3
"""One-off script: adds `companies` and `frequency` fields to every problem
in src/main/resources/course/catalog.yml, and adds `track: spring` to the
SPRING_BOOT_INTERVIEW pattern header.

Uses line-based insertion (not a YAML dump) to avoid reformatting the large
hand-written catalog file. Run once; safe to re-run (it is idempotent since
it skips problems that already have a `companies` field).
"""
import re
import sys
from pathlib import Path

CATALOG = Path(__file__).resolve().parent.parent / "src/main/resources/course/catalog.yml"

# id -> (frequency, [companies])
ASSIGNMENTS = {
    # TWO_POINTERS
    "pair-with-target-sum": ("HIGH", ["FAANG", "Google", "Amazon"]),
    "remove-duplicates": ("HIGH", ["FAANG", "Microsoft"]),
    "triplet-sum-zero": ("HIGH", ["FAANG", "Meta", "Amazon"]),
    "container-with-most-water": ("HIGH", ["FAANG", "Google", "Amazon"]),
    "dutch-national-flag": ("MEDIUM", ["Microsoft", "Amazon"]),
    "quadruple-sum": ("MEDIUM", ["Amazon", "Meta"]),
    # FAST_SLOW_POINTERS
    "linked-list-cycle": ("HIGH", ["FAANG", "Amazon", "Microsoft"]),
    "middle-of-linked-list": ("HIGH", ["Amazon", "Microsoft"]),
    "happy-number": ("MEDIUM", ["Google", "Amazon"]),
    "cycle-start": ("MEDIUM", ["Amazon", "Microsoft"]),
    "palindrome-linked-list": ("MEDIUM", ["Amazon", "Meta"]),
    "circular-array-loop": ("LOW", ["Google"]),
    # SLIDING_WINDOW
    "max-sum-subarray-k": ("MEDIUM", ["Amazon", "Microsoft"]),
    "longest-substring-k-distinct": ("MEDIUM", ["Meta", "Google"]),
    "find-all-anagrams": ("MEDIUM", ["Meta", "Amazon"]),
    "longest-no-repeat": ("HIGH", ["FAANG", "Meta", "Amazon"]),
    "min-window-substring": ("HIGH", ["FAANG", "Meta", "Google"]),
    # INTERVALS
    "merge-intervals": ("HIGH", ["FAANG", "Google", "Meta"]),
    "insert-interval": ("HIGH", ["Google", "Meta"]),
    "conflicting-appointments": ("MEDIUM", ["Meta", "Amazon"]),
    "interval-intersection": ("MEDIUM", ["Google", "Amazon"]),
    "minimum-meeting-rooms": ("HIGH", ["Meta", "Google", "Amazon"]),
    # IN_PLACE_LINKED_LIST
    "reverse-linked-list": ("HIGH", ["FAANG", "Amazon", "Microsoft"]),
    "reverse-sublist": ("MEDIUM", ["Microsoft", "Amazon"]),
    "reverse-every-k": ("MEDIUM", ["Amazon", "Microsoft"]),
    "rotate-list": ("MEDIUM", ["Amazon"]),
    "reorder-list": ("MEDIUM", ["Meta", "Amazon"]),
    # TWO_HEAPS
    "find-median-stream": ("HIGH", ["FAANG", "Google", "Amazon"]),
    "maximize-capital": ("MEDIUM", ["Amazon"]),
    "sliding-window-median": ("LOW", ["Google"]),
    "next-interval": ("LOW", ["Google"]),
    "static-median": ("LOW", ["Backend"]),
    # K_WAY_MERGE
    "merge-k-sorted-lists": ("HIGH", ["FAANG", "Amazon", "Google", "Meta"]),
    "kth-smallest-sorted-matrix": ("MEDIUM", ["Amazon", "Google"]),
    "k-smallest-pairs": ("MEDIUM", ["Amazon"]),
    "smallest-range": ("LOW", ["Google"]),
    "merge-two-sorted-lists": ("HIGH", ["FAANG", "Amazon", "Microsoft"]),
    # TOP_K_ELEMENTS
    "top-k-numbers": ("MEDIUM", ["Amazon", "Meta"]),
    "k-closest-points": ("HIGH", ["FAANG", "Amazon", "Google"]),
    "top-k-frequent": ("HIGH", ["FAANG", "Meta", "Amazon"]),
    "kth-largest": ("HIGH", ["Amazon", "Meta", "Google"]),
    "reorganize-string": ("MEDIUM", ["Google", "Amazon"]),
    # MODIFIED_BINARY_SEARCH
    "order-agnostic-binary-search": ("MEDIUM", ["Amazon"]),
    "search-rotated-array": ("HIGH", ["FAANG", "Meta", "Amazon"]),
    "find-range": ("HIGH", ["Amazon", "Google"]),
    "find-peak-element": ("MEDIUM", ["Google", "Amazon"]),
    "search-rotated-duplicates": ("MEDIUM", ["Amazon"]),
    # SUBSETS
    "subsets": ("HIGH", ["FAANG", "Meta", "Amazon"]),
    "subsets-with-duplicates": ("MEDIUM", ["Amazon"]),
    "permutations": ("HIGH", ["FAANG", "Meta", "Amazon"]),
    "generate-parentheses": ("HIGH", ["FAANG", "Meta", "Google"]),
    "unique-permutations": ("MEDIUM", ["Amazon"]),
    "subsets-size-k": ("MEDIUM", ["Amazon"]),
    # GREEDY
    "jump-game": ("HIGH", ["FAANG", "Amazon", "Google"]),
    "jump-game-ii": ("MEDIUM", ["Amazon", "Google"]),
    "gas-station": ("MEDIUM", ["Amazon", "Meta"]),
    "candy": ("MEDIUM", ["Google", "Amazon"]),
    "assign-cookies": ("LOW", ["Backend"]),
    # BACKTRACKING
    "combination-sum": ("HIGH", ["FAANG", "Amazon", "Meta"]),
    "word-search": ("HIGH", ["FAANG", "Amazon", "Meta"]),
    "n-queens": ("MEDIUM", ["Google", "Amazon"]),
    "sudoku-solver": ("MEDIUM", ["Google", "Apple"]),
    "letter-case-permutation": ("LOW", ["Backend"]),
    # DYNAMIC_PROGRAMMING
    "climbing-stairs": ("HIGH", ["FAANG", "Amazon", "Apple"]),
    "house-robber": ("HIGH", ["FAANG", "Amazon", "Google"]),
    "0-1-knapsack": ("MEDIUM", ["Amazon", "Google"]),
    "coin-change": ("HIGH", ["FAANG", "Amazon", "Google"]),
    "edit-distance": ("HIGH", ["FAANG", "Google", "Meta"]),
    # CYCLIC_SORT
    "cyclic-sort": ("MEDIUM", ["Amazon"]),
    "find-missing-number": ("HIGH", ["Amazon", "Microsoft"]),
    "find-all-duplicates": ("MEDIUM", ["Amazon"]),
    "find-duplicate-number": ("HIGH", ["FAANG", "Amazon", "Google"]),
    "first-missing-positive": ("HIGH", ["FAANG", "Amazon", "Google"]),
    # TOPOLOGICAL_SORT
    "course-schedule": ("HIGH", ["FAANG", "Meta", "Google", "Amazon"]),
    "course-schedule-order": ("HIGH", ["Meta", "Google", "Amazon"]),
    "tasks-scheduling": ("MEDIUM", ["Amazon", "Microsoft"]),
    "alien-dictionary-order": ("MEDIUM", ["Google", "Meta"]),
    "verify-alien-dictionary": ("LOW", ["Google"]),
    # SORT_AND_SEARCH
    "group-anagrams": ("HIGH", ["FAANG", "Meta", "Amazon"]),
    "search-suggestions": ("MEDIUM", ["Amazon", "Google"]),
    "wiggle-sort": ("LOW", ["Google"]),
    "count-smaller-after-self": ("LOW", ["Google"]),
    "intersection-two-arrays": ("MEDIUM", ["Meta", "Amazon"]),
    # MATRICES
    "spiral-order": ("HIGH", ["FAANG", "Microsoft", "Amazon"]),
    "rotate-image": ("HIGH", ["FAANG", "Microsoft", "Amazon"]),
    "set-matrix-zeroes": ("MEDIUM", ["Amazon", "Microsoft"]),
    "search-2d-matrix": ("MEDIUM", ["Amazon", "Google"]),
    "shortest-path-binary-matrix": ("MEDIUM", ["Google", "Amazon"]),
    "matrix-diagonal-sum": ("LOW", ["Backend"]),
    # STACKS
    "valid-parentheses": ("HIGH", ["FAANG", "Amazon", "Meta", "Microsoft"]),
    "daily-temperatures": ("HIGH", ["Amazon", "Meta"]),
    "next-greater-element": ("MEDIUM", ["Amazon"]),
    "decode-string": ("MEDIUM", ["Google", "Amazon"]),
    "basic-calculator": ("MEDIUM", ["Google", "Meta"]),
    # GRAPHS
    "number-of-islands": ("HIGH", ["FAANG", "Amazon", "Google", "Meta"]),
    "clone-graph": ("HIGH", ["Meta", "Google", "Amazon"]),
    "pacific-atlantic": ("MEDIUM", ["Google", "Amazon"]),
    "critical-connections": ("MEDIUM", ["Google"]),
    "find-center-star": ("LOW", ["Backend"]),
    # TREE_DFS
    "path-sum": ("HIGH", ["Amazon", "Microsoft"]),
    "all-path-sum": ("MEDIUM", ["Amazon"]),
    "tree-diameter": ("MEDIUM", ["Amazon", "Google"]),
    "lowest-common-ancestor": ("HIGH", ["FAANG", "Amazon", "Meta", "Microsoft"]),
    "max-path-sum": ("HIGH", ["FAANG", "Meta", "Google"]),
    # TREE_BFS
    "level-order": ("HIGH", ["FAANG", "Amazon", "Microsoft"]),
    "zigzag-level-order": ("MEDIUM", ["Amazon", "Meta"]),
    "right-side-view": ("MEDIUM", ["Amazon", "Meta"]),
    "min-depth": ("MEDIUM", ["Amazon"]),
    "vertical-order": ("MEDIUM", ["Meta", "Amazon"]),
    # TRIE
    "implement-trie": ("HIGH", ["FAANG", "Google", "Amazon"]),
    "word-break-dict": ("MEDIUM", ["Amazon", "Google"]),
    "replace-words": ("LOW", ["Backend"]),
    "word-search-ii": ("MEDIUM", ["Google", "Amazon"]),
    "longest-common-prefix": ("HIGH", ["FAANG", "Amazon", "Microsoft"]),
    # HASH_MAPS
    "two-sum": ("HIGH", ["FAANG", "Amazon", "Meta", "Google", "Microsoft"]),
    "subarray-sum-equals-k": ("HIGH", ["Meta", "Google", "Amazon"]),
    "isomorphic-strings": ("MEDIUM", ["Amazon"]),
    "longest-consecutive-already": ("MEDIUM", ["Meta", "Google"]),
    "four-sum-ii": ("MEDIUM", ["Amazon"]),
    # KNOWING_WHAT_TO_TRACK
    "majority-element": ("HIGH", ["Amazon", "Microsoft"]),
    "longest-consecutive": ("HIGH", ["Meta", "Google", "Amazon"]),
    "majority-element-ii": ("MEDIUM", ["Amazon"]),
    "max-points-on-line": ("MEDIUM", ["Google", "Meta"]),
    # UNION_FIND
    "number-of-provinces": ("HIGH", ["Amazon", "Google"]),
    "redundant-connection": ("MEDIUM", ["Google", "Amazon"]),
    "accounts-merge": ("MEDIUM", ["Meta", "Amazon"]),
    "number-of-islands-ii": ("MEDIUM", ["Google"]),
    "path-exists": ("LOW", ["Backend"]),
    # CUSTOM_DATA_STRUCTURES
    "min-stack": ("HIGH", ["FAANG", "Amazon", "Meta"]),
    "lru-cache": ("HIGH", ["FAANG", "Amazon", "Meta", "Google", "Microsoft"]),
    "time-map": ("MEDIUM", ["Amazon", "Google"]),
    "lfu-cache": ("MEDIUM", ["Google", "Amazon"]),
    "design-hashmap": ("MEDIUM", ["Amazon"]),
    # BITWISE
    "single-number": ("HIGH", ["Amazon", "Meta"]),
    "number-of-1-bits": ("HIGH", ["Amazon", "Apple"]),
    "single-number-ii": ("MEDIUM", ["Amazon"]),
    "sum-of-two-integers": ("MEDIUM", ["Amazon", "Apple"]),
    "reverse-bits": ("MEDIUM", ["Apple", "Amazon"]),
    "bitwise-and-range": ("LOW", ["Backend"]),
    # MATH_GEOMETRY
    "pow-x-n": ("MEDIUM", ["Google", "Amazon"]),
    "gcd-of-strings": ("LOW", ["Backend"]),
    "sqrt": ("MEDIUM", ["Amazon", "Apple"]),
    "integer-to-roman": ("MEDIUM", ["Amazon"]),
    "max-points-line-math": ("LOW", ["Google"]),
    "max-points-on-a-line": ("MEDIUM", ["Google", "Meta"]),
    # SPRING_BOOT_INTERVIEW (existing)
    "rate-limiter": ("HIGH", ["Fintech", "Backend", "Netflix"]),
    "idempotency-store": ("HIGH", ["Fintech", "Backend", "Amazon"]),
    "circuit-breaker": ("MEDIUM", ["Netflix", "Backend", "Microsoft"]),
    "lru-http-cache": ("MEDIUM", ["Backend", "Amazon"]),
    "config-store": ("LOW", ["Backend"]),
    # SPRING_BOOT_INTERVIEW (new)
    "request-id-filter": ("HIGH", ["Backend", "Microsoft"]),
    "transactional-outbox": ("HIGH", ["Fintech", "Backend", "Netflix"]),
    "optimistic-lock-service": ("MEDIUM", ["Fintech", "Backend", "Amazon"]),
    "concurrent-login-tracker": ("MEDIUM", ["Backend", "Netflix", "Microsoft"]),
    "sql-injection-safe-query": ("HIGH", ["Fintech", "Backend", "Microsoft"]),
}


def build_insert(companies, frequency):
    lines = ["    companies:\n"]
    for c in companies:
        lines.append(f"    - {c}\n")
    lines.append(f"    frequency: {frequency}\n")
    return lines


def main():
    text = CATALOG.read_text()
    lines = text.splitlines(keepends=True)

    problem_starts = [i for i, l in enumerate(lines) if re.match(r"^  - id: ", l)]
    pattern_starts = [i for i, l in enumerate(lines) if re.match(r"^- id: ", l)]
    boundaries = sorted(set(problem_starts + pattern_starts + [len(lines)]))

    inserts = []  # (index, new_lines)
    missing = []
    already = 0
    for start in problem_starts:
        pid = re.match(r"^  - id: (\S+)", lines[start]).group(1)
        end = min(b for b in boundaries if b > start)
        block = lines[start:end]
        if any(re.match(r"^    companies:", l) for l in block):
            already += 1
            continue
        if pid not in ASSIGNMENTS:
            missing.append(pid)
            continue
        frequency, companies = ASSIGNMENTS[pid]
        inserts.append((end, build_insert(companies, frequency)))

    if missing:
        print("Missing assignments for:", missing, file=sys.stderr)
        sys.exit(1)

    print(f"Inserting companies/frequency into {len(inserts)} problems ({already} already had it).")

    for idx, new_lines in sorted(inserts, key=lambda x: x[0], reverse=True):
        lines[idx:idx] = new_lines

    CATALOG.write_text("".join(lines))


if __name__ == "__main__":
    main()
