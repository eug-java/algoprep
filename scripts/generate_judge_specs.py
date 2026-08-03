#!/usr/bin/env python3
"""Mass-generate judge YAML specs from catalog + Java sources/tests."""

from __future__ import annotations

import re
import sys
from pathlib import Path
from typing import Any

import yaml

ROOT = Path(__file__).resolve().parents[1]
CATALOG = ROOT / "src/main/resources/course/catalog.yml"
SPECS_DIR = ROOT / "src/main/resources/judge/specs"
SRC_ROOT = ROOT / "src/main/java"
TEST_ROOT = ROOT / "src/test/java"

STATEFUL_SKIP = {
    "find-median-stream",
    "implement-trie",
    "min-stack",
    "time-map",
    "design-hashmap",
    "circuit-breaker",
    "config-store",
    "request-id-filter",
    "transactional-outbox",
    "optimistic-lock-service",
    "concurrent-login-tracker",
    "lru-cache",
    "lfu-cache",
    "rate-limiter",
    "idempotency-store",
    "lru-http-cache",
}

# Node / interval types differ between judge helpers (default package) and
# com.algoprep.common.* used by reference classes — keep explicit expected.
NO_REFERENCE_TYPES = {
    "ListNode",
    "TreeNode",
    "Interval",
    "ListNode[]",
    "Interval[]",
    "List<Interval>",
    "GraphNode",
    "BuiltQuery",
    "Map<String, Object>",
    "Optional<String>",
}

STATIC_METHOD_RE = re.compile(
    r"public\s+static\s+([\w.<>,\[\]\s]+?)\s+(\w+)\s*\(([^;{}]*)\)\s*\{",
    re.MULTILINE,
)
INSTANCE_METHOD_RE = re.compile(
    r"public\s+(?!static)([\w.<>,\[\]\s]+?)\s+(\w+)\s*\(([^;{}]*)\)\s*\{",
    re.MULTILINE,
)

FALLBACK_CASES: dict[str, list[dict[str, Any]]] = {
    "pair-with-target-sum": [
        {"name": "example1", "args": [[1, 2, 3, 4, 6], 6]},
        {"name": "absent", "args": [[2, 5, 9, 11], 12]},
    ],
    "remove-duplicates": [
        {"name": "duplicates", "args": [[2, 3, 3, 3, 6, 9, 9]]},
        {"name": "all-unique", "args": [[1, 2, 3]]},
    ],
    "triplet-sum-zero": [
        {"name": "example", "args": [[-3, 0, 1, 2, -1, 1, -2]]},
        {"name": "short", "args": [[1, 2]]},
    ],
    "container-with-most-water": [
        {"name": "example", "args": [[1, 8, 6, 2, 5, 4, 8, 3, 7]]},
        {"name": "small", "args": [[1, 1]]},
    ],
    "dutch-national-flag": [
        {"name": "mixed", "args": [[1, 0, 2, 1, 0]], "expected": [0, 0, 1, 1, 2]},
        {"name": "sorted", "args": [[0, 1, 2]], "expected": [0, 1, 2]},
    ],
    "quadruple-sum": [
        {"name": "example", "args": [[4, 1, 2, -1, 1, -3], 1]},
        {"name": "none", "args": [[2, 0, -1], 3]},
    ],
    "happy-number": [
        {"name": "happy", "args": [19]},
        {"name": "unhappy", "args": [2]},
    ],
    "max-sum-subarray-k": [
        {"name": "example", "args": [[2, 1, 5, 1, 3, 2], 3]},
        {"name": "another", "args": [[2, 3, 4, 1, 5], 2]},
    ],
    "longest-substring-k-distinct": [
        {"name": "example", "args": ["araaci", 2]},
        {"name": "all-same", "args": ["aaaa", 1]},
    ],
    "find-all-anagrams": [
        {"name": "example", "args": ["cbaebabacd", "abc"]},
        {"name": "none", "args": ["abab", "xy"]},
    ],
    "longest-no-repeat": [
        {"name": "example", "args": ["aabccbb"]},
        {"name": "all-unique", "args": ["abcdef"]},
    ],
    "min-window-substring": [
        {"name": "example", "args": ["ADOBECODEBANC", "ABC"]},
        {"name": "same", "args": ["a", "a"]},
    ],
    "climbing-stairs": [
        {"name": "n3", "args": [3]},
        {"name": "n4", "args": [4]},
    ],
    "two-sum": [
        {"name": "example", "args": [[2, 7, 11, 15], 9]},
        {"name": "another", "args": [[3, 2, 4], 6]},
    ],
    "valid-parentheses": [
        {"name": "valid", "args": ["()[]{}"]},
        {"name": "invalid", "args": ["(]"]},
    ],
    "single-number": [
        {"name": "example", "args": [[4, 1, 2, 1, 2]]},
        {"name": "single", "args": [[1]]},
    ],
    "number-of-islands": [
        {
            "name": "separate-islands",
            "args": [["11000", "11000", "00100", "00011"]],
        },
        {"name": "empty", "args": [[]]},
    ],
    "binary-search": [
        {"name": "found", "args": [[1, 2, 3, 4, 5], 3]},
        {"name": "missing", "args": [[1, 2, 3, 4, 5], 6]},
    ],
    "search-rotated": [
        {"name": "found", "args": [[4, 5, 6, 7, 0, 1, 2], 0]},
        {"name": "missing", "args": [[4, 5, 6, 7, 0, 1, 2], 3]},
    ],
    "find-minimum-rotated": [
        {"name": "example", "args": [[3, 4, 5, 1, 2]]},
        {"name": "sorted", "args": [[1, 2, 3]]},
    ],
    "search-range": [
        {"name": "found", "args": [[5, 7, 7, 8, 8, 10], 8]},
        {"name": "missing", "args": [[5, 7, 7, 8, 8, 10], 6]},
    ],
    "kth-smallest-in-matrix": [
        {"name": "example", "args": [[[1, 5, 9], [10, 11, 13], [12, 13, 15]], 8]},
    ],
    "merge-intervals": [
        {
            "name": "merge",
            "args": [[[2, 5], [1, 3], [7, 9]]],
            "expected": [[1, 5], [7, 9]],
        },
        {"name": "empty", "args": [[]], "expected": []},
    ],
    "insert-interval": [
        {
            "name": "insert",
            "args": [[[1, 3], [6, 9]], [2, 5]],
            "expected": [[1, 5], [6, 9]],
        }
    ],
    "conflicting-appointments": [
        {"name": "conflict", "args": [[[1, 4], [2, 5], [7, 9]]], "expected": False},
        {"name": "ok", "args": [[[1, 4], [5, 6]]], "expected": True},
    ],
    "interval-intersection": [
        {
            "name": "overlap",
            "args": [[[0, 2], [5, 10]], [[1, 5], [8, 12]]],
            "expected": [[1, 2], [5, 5], [8, 10]],
        }
    ],
    "minimum-meeting-rooms": [
        {"name": "rooms", "args": [[[1, 4], [2, 5], [7, 9]]]},
        {"name": "one", "args": [[[1, 2], [3, 4]]]},
    ],
    "cyclic-sort": [
        {"name": "sort", "args": [[3, 1, 5, 4, 2]], "expected": [1, 2, 3, 4, 5]},
    ],
    "find-missing-number": [
        {"name": "missing", "args": [[4, 0, 3, 1]]},
        {"name": "none-missing-end", "args": [[0, 1, 2]]},
    ],
    "find-duplicate": [
        {"name": "dup", "args": [[1, 4, 4, 3, 2]]},
        {"name": "another", "args": [[2, 1, 3, 3, 5, 4]]},
    ],
    "find-all-duplicates": [
        {"name": "dups", "args": [[3, 4, 4, 5, 5]]},
        {"name": "none", "args": [[1, 2, 3]]},
    ],
    "find-corrupt-pair": [
        {"name": "pair", "args": [[3, 1, 2, 5, 2]]},
    ],
    "linked-list-cycle": [
        {"name": "no-cycle", "args": [[1, 2, 3, 4]], "expected": False},
    ],
    "middle-of-linked-list": [
        {"name": "odd", "args": [[1, 2, 3, 4, 5]], "expected": [3, 4, 5]},
        {"name": "even", "args": [[1, 2, 3, 4]], "expected": [3, 4]},
    ],
    "reverse-linked-list": [
        {"name": "reverse", "args": [[1, 2, 3, 4]], "expected": [4, 3, 2, 1]},
        {"name": "empty", "args": [[]], "expected": None},
    ],
    "merge-two-sorted-lists": [
        {
            "name": "merge",
            "args": [[1, 2, 4], [1, 3, 4]],
            "expected": [1, 1, 2, 3, 4, 4],
        }
    ],
    "path-sum": [
        {
            "name": "path-exists",
            "args": [[5, 4, 8, 11, None, 13, 4, 7, 2, None, None, None, 1], 22],
            "expected": True,
        },
        {"name": "path-missing", "args": [[1, 2, 3], 5], "expected": False},
    ],
    "level-order": [
        {"name": "example", "args": [[1, 2, 3, 4, 5]], "expected": [[1], [2, 3], [4, 5]]},
        {"name": "empty", "args": [None], "expected": []},
    ],
    "min-depth": [
        {"name": "example", "args": [[1, 2, 3, 4]], "expected": 2},
    ],
    "house-robber": [
        {"name": "example", "args": [[2, 7, 9, 3, 1]]},
        {"name": "two", "args": [[1, 2]]},
    ],
    "coin-change": [
        {"name": "example", "args": [[1, 2, 5], 11]},
        {"name": "impossible", "args": [[2], 3]},
    ],
    "longest-common-subsequence": [
        {"name": "example", "args": ["abcde", "ace"]},
        {"name": "none", "args": ["abc", "def"]},
    ],
    "edit-distance": [
        {"name": "example", "args": ["horse", "ros"]},
        {"name": "same", "args": ["a", "a"]},
    ],
    "knapsack": [
        {"name": "example", "args": [[1, 2, 3], [6, 10, 12], 5]},
    ],
    "subset-sum": [
        {"name": "yes", "args": [[1, 2, 3, 7], 6]},
        {"name": "no", "args": [[1, 2, 7], 11]},
    ],
    "target-sum": [
        {"name": "example", "args": [[1, 1, 1, 1, 1], 3]},
    ],
    "unique-paths": [
        {"name": "grid", "args": [3, 7]},
        {"name": "square", "args": [3, 2]},
    ],
    "word-break": [
        {"name": "yes", "args": ["leetcode", ["leet", "code"]]},
        {"name": "no", "args": ["catsandog", ["cats", "dog", "sand", "and", "cat"]]},
    ],
    "combination-sum": [
        {"name": "example", "args": [[2, 3, 6, 7], 7]},
    ],
    "permutations": [
        {"name": "example", "args": [[1, 2, 3]]},
    ],
    "subsets": [
        {"name": "example", "args": [[1, 2, 3]]},
    ],
    "generate-parentheses": [
        {"name": "n2", "args": [2]},
        {"name": "n1", "args": [1]},
    ],
    "letter-case-permutation": [
        {"name": "example", "args": ["a1b2"]},
    ],
    "sudoku-solver": [
        {
            "name": "board",
            "args": [
                [
                    "53..7....",
                    "6..195...",
                    ".98....6.",
                    "8...6...3",
                    "4..8.3..1",
                    "7...2...6",
                    ".6....28.",
                    "...419..5",
                    "....8..79",
                ]
            ],
        }
    ],
    "rotate-image": [
        {
            "name": "3x3",
            "args": [[[1, 2, 3], [4, 5, 6], [7, 8, 9]]],
            "expected": [[7, 4, 1], [8, 5, 2], [9, 6, 3]],
        }
    ],
    "set-matrix-zeroes": [
        {
            "name": "zero",
            "args": [[[1, 1, 1], [1, 0, 1], [1, 1, 1]]],
            "expected": [[1, 0, 1], [0, 0, 0], [1, 0, 1]],
        }
    ],
    "wiggle-sort": [
        {"name": "wiggle", "args": [[3, 5, 2, 1, 6, 4]]},
    ],
    "kth-largest": [
        {"name": "example", "args": [[3, 2, 1, 5, 6, 4], 2]},
    ],
    "top-k-frequent": [
        {"name": "example", "args": [[1, 1, 1, 2, 2, 3], 2]},
    ],
    "connect-ropes": [
        {"name": "example", "args": [[1, 3, 11, 5]]},
    ],
    "frequency-sort": [
        {"name": "example", "args": ["tree"]},
    ],
    "task-scheduler": [
        {"name": "example", "args": [["A", "A", "A", "B", "B", "B"], 2]},
    ],
    "word-search": [
        {"name": "found", "args": [["ABCE", "SFCS", "ADEE"], "ABCCED"]},
        {"name": "missing", "args": [["ABCE", "SFCS", "ADEE"], "ABCB"]},
    ],
    "search-2d-matrix": [
        {
            "name": "found",
            "args": [[[1, 4, 7, 11], [2, 5, 8, 12], [3, 6, 9, 16]], 8],
        },
        {
            "name": "missing",
            "args": [[[1, 4, 7, 11], [2, 5, 8, 12], [3, 6, 9, 16]], 10],
        },
    ],
    "critical-connections": [
        {"name": "bridge", "args": [4, [[0, 1], [1, 2], [2, 0], [1, 3]]]},
        {"name": "cycle", "args": [3, [[0, 1], [1, 2], [2, 0]]]},
    ],
    "minimum-meeting-rooms": [
        {"name": "two-rooms", "args": [[[1, 4], [2, 5], [7, 9]]], "expected": 2},
        {"name": "empty", "args": [[]], "expected": 0},
    ],
    "cycle-start": [
        {"name": "acyclic", "args": [[1, 2]], "expected": None},
    ],
    "palindrome-linked-list": [
        {"name": "palindrome", "args": [[1, 2, 2, 1]], "expected": True},
        {"name": "not", "args": [[1, 2]], "expected": False},
    ],
    "reverse-sublist": [
        {"name": "reverse", "args": [[1, 2, 3, 4, 5], 2, 4], "expected": [1, 4, 3, 2, 5]},
    ],
    "reverse-every-k": [
        {"name": "k2", "args": [[1, 2, 3, 4, 5], 2], "expected": [2, 1, 4, 3, 5]},
    ],
    "rotate-list": [
        {"name": "rotate", "args": [[1, 2, 3, 4, 5], 2], "expected": [4, 5, 1, 2, 3]},
    ],
    "reorder-list": [
        {"name": "reorder", "args": [[1, 2, 3, 4]], "expected": [1, 4, 2, 3]},
    ],
    "next-interval": [
        {"name": "example", "args": [[[2, 3], [3, 4], [5, 6]]], "expected": [1, 2, -1]},
    ],
    "merge-k-sorted-lists": [
        {
            "name": "merge",
            "args": [[[1, 4, 5], [1, 3, 4], [2, 6]]],
            "expected": [1, 1, 2, 3, 4, 4, 5, 6],
        }
    ],
    "all-path-sum": [
        {
            "name": "paths",
            "args": [[5, 4, 8, 11, None, 13, 4, 7, 2, None, None, 5, 1], 22],
            "expected": [[5, 4, 11, 2], [5, 8, 4, 5]],
        }
    ],
    "tree-diameter": [
        {"name": "diameter", "args": [[1, 2, 3, 4, 5]], "expected": 3},
    ],
    "max-path-sum": [
        {"name": "example", "args": [[1, 2, 3]], "expected": 6},
    ],
    "zigzag-level-order": [
        {
            "name": "zigzag",
            "args": [[1, 2, 3, 4, 5]],
            "expected": [[1], [3, 2], [4, 5]],
        }
    ],
    "right-side-view": [
        {"name": "view", "args": [[1, 2, 3, None, 5, None, 4]], "expected": [1, 3, 4]},
    ],
    "vertical-order": [
        {
            "name": "vertical",
            "args": [[3, 9, 20, None, None, 15, 7]],
            "expected": [[9], [3, 15], [20], [7]],
        }
    ],
    "word-search-ii": [
        {
            "name": "words",
            "args": [["oaan", "etae", "ihkr", "iflv"], ["oath", "pea", "eat", "rain"]],
        }
    ],
}


def load_catalog() -> list[dict[str, Any]]:
    data = yaml.safe_load(CATALOG.read_text())
    out = []
    for pattern in data.get("patterns", []):
        for problem in pattern.get("problems", []):
            out.append({**problem, "patternId": pattern["id"]})
    return out


def source_path(class_name: str) -> Path:
    return SRC_ROOT / Path(*class_name.split(".")).with_suffix(".java")


def test_path(class_name: str) -> Path:
    return TEST_ROOT / Path(*class_name.split(".")).with_suffix(".java").with_name(
        class_name.split(".")[-1] + "Test.java"
    )


def normalize_type(type_str: str) -> str:
    return re.sub(r"\s+", "", type_str.strip())


def parse_params(params_src: str) -> list[str]:
    params_src = params_src.strip()
    if not params_src:
        return []
    parts: list[str] = []
    depth = 0
    current = []
    for ch in params_src:
        if ch == "<":
            depth += 1
        elif ch == ">":
            depth -= 1
        if ch == "," and depth == 0:
            parts.append("".join(current).strip())
            current = []
        else:
            current.append(ch)
    if current:
        parts.append("".join(current).strip())
    types = []
    for part in parts:
        part = re.sub(r"\s+", " ", part).strip()
        # drop parameter name
        tokens = part.split(" ")
        if len(tokens) >= 2:
            types.append(normalize_type(" ".join(tokens[:-1])))
        else:
            types.append(normalize_type(part))
    return types


def extract_primary_method(java_source: str, simple_name: str) -> tuple[str, str, list[str], bool] | None:
    static_methods = []
    for match in STATIC_METHOD_RE.finditer(java_source):
        returns, name, params = match.group(1), match.group(2), match.group(3)
        if name in {"main"}:
            continue
        static_methods.append((normalize_type(returns), name, parse_params(params), True))
    if static_methods:
        # Prefer the first public static algorithmic method.
        return static_methods[0]

    instance_methods = []
    for match in INSTANCE_METHOD_RE.finditer(java_source):
        returns, name, params = match.group(1), match.group(2), match.group(3)
        if name == simple_name:
            continue
        instance_methods.append((normalize_type(returns), name, parse_params(params), False))
    if len(instance_methods) == 1:
        return instance_methods[0]
    return None


def split_args(args_src: str) -> list[str]:
    parts: list[str] = []
    current: list[str] = []
    depth = 0
    in_str = False
    escape = False
    for ch in args_src:
        if in_str:
            current.append(ch)
            if escape:
                escape = False
            elif ch == "\\":
                escape = True
            elif ch == '"':
                in_str = False
            continue
        if ch == '"':
            in_str = True
            current.append(ch)
            continue
        if ch in "({[<":
            depth += 1
            current.append(ch)
            continue
        if ch in ")}]>":
            depth -= 1
            current.append(ch)
            continue
        if ch == "," and depth == 0:
            parts.append("".join(current).strip())
            current = []
            continue
        current.append(ch)
    if current and "".join(current).strip():
        parts.append("".join(current).strip())
    return parts


def java_literal_to_yaml(expr: str) -> Any:
    expr = expr.strip()
    if not expr:
        raise ValueError("empty expr")
    if expr == "null":
        return None
    if expr in {"true", "false"}:
        return expr == "true"
    if re.fullmatch(r"-?\d+", expr):
        return int(expr)
    if re.fullmatch(r"-?\d+\.\d+", expr):
        return float(expr)
    if expr.startswith('"') and expr.endswith('"'):
        return bytes(expr[1:-1], "utf-8").decode("unicode_escape")
    if expr.startswith("'") and expr.endswith("'") and len(expr) >= 3:
        return expr[1:-1]

    m = re.fullmatch(r"new\s+int\s*\[\s*\]\s*\{(.*)\}", expr, re.S)
    if m:
        inner = m.group(1).strip()
        if not inner:
            return []
        return [java_literal_to_yaml(p) for p in split_args(inner)]

    m = re.fullmatch(r"new\s+int\s*\[\s*\]\s*\[\s*\]\s*\{(.*)\}", expr, re.S)
    if m:
        inner = m.group(1).strip()
        if not inner:
            return []
        return [java_literal_to_yaml(p) for p in split_args(inner)]

    m = re.fullmatch(r"new\s+char\s*\[\s*\]\s*\[\s*\]\s*\{(.*)\}", expr, re.S)
    if m:
        rows = []
        for part in split_args(m.group(1)):
            part = part.strip()
            if part.endswith(".toCharArray()"):
                rows.append(java_literal_to_yaml(part[: -len(".toCharArray()")]))
            elif part.startswith('"') and part.endswith('"'):
                rows.append(java_literal_to_yaml(part))
            else:
                raise ValueError(f"unsupported char[][] row: {part}")
        return rows

    m = re.fullmatch(r"new\s+String\s*\[\s*\]\s*\{(.*)\}", expr, re.S)
    if m:
        inner = m.group(1).strip()
        if not inner:
            return []
        return [java_literal_to_yaml(p) for p in split_args(inner)]

    m = re.fullmatch(r"(?:java\.util\.)?List\.of\s*\((.*)\)", expr, re.S)
    if m:
        inner = m.group(1).strip()
        if not inner:
            return []
        return [java_literal_to_yaml(p) for p in split_args(inner)]

    m = re.fullmatch(r"Arrays\.asList\s*\((.*)\)", expr, re.S)
    if m:
        inner = m.group(1).strip()
        if not inner:
            return []
        return [java_literal_to_yaml(p) for p in split_args(inner)]

    m = re.fullmatch(r"(?:TreeNode|ListNode)\.of\s*\((.*)\)", expr, re.S)
    if m:
        inner = m.group(1).strip()
        if not inner:
            return []
        return [java_literal_to_yaml(p) for p in split_args(inner)]

    m = re.fullmatch(r"new\s+Interval\s*\((.*)\)", expr, re.S)
    if m:
        parts = split_args(m.group(1))
        if len(parts) != 2:
            raise ValueError(expr)
        return [java_literal_to_yaml(parts[0]), java_literal_to_yaml(parts[1])]

    m = re.fullmatch(r"new\s+Interval\s*\[\s*\]\s*\{(.*)\}", expr, re.S)
    if m:
        inner = m.group(1).strip()
        if not inner:
            return []
        return [java_literal_to_yaml(p) for p in split_args(inner)]

    # int[] x = {1,2,3} style already extracted elsewhere; bare {1,2}
    m = re.fullmatch(r"\{(.*)\}", expr, re.S)
    if m and "new " not in expr:
        inner = m.group(1).strip()
        if not inner:
            return []
        return [java_literal_to_yaml(p) for p in split_args(inner)]

    raise ValueError(f"unsupported literal: {expr[:120]}")


def extract_cases_from_test(test_source: str, class_simple: str, method: str) -> list[dict[str, Any]]:
    cases: list[dict[str, Any]] = []
    pattern = re.compile(rf"{re.escape(class_simple)}\.{re.escape(method)}\s*\(")
    for match in pattern.finditer(test_source):
        start = match.end()
        depth = 1
        i = start
        in_str = False
        escape = False
        while i < len(test_source) and depth:
            ch = test_source[i]
            if in_str:
                if escape:
                    escape = False
                elif ch == "\\":
                    escape = True
                elif ch == '"':
                    in_str = False
            else:
                if ch == '"':
                    in_str = True
                elif ch == "(":
                    depth += 1
                elif ch == ")":
                    depth -= 1
            i += 1
        if depth != 0:
            continue
        args_src = test_source[start : i - 1]
        try:
            arg_exprs = split_args(args_src)
            args = [java_literal_to_yaml(expr) for expr in arg_exprs]
        except Exception:
            continue
        # Skip calls that clearly use non-literal variables only.
        if any(re.fullmatch(r"[A-Za-z_][\w]*", expr.strip()) and expr.strip() not in {"null", "true", "false"}
               for expr in arg_exprs):
            # Allow if we still parsed something useful via constructors; variable names fail literal parse.
            pass
        cases.append({"name": f"case{len(cases)+1}", "args": args})
        if len(cases) >= 5:
            break
    return cases


def needs_helper(params: list[str], returns: str) -> list[str]:
    helpers = []
    blob = ",".join(params + [returns])
    for helper in ("ListNode", "TreeNode", "Interval", "GraphNode"):
        if helper in blob:
            helpers.append(helper)
    return helpers


def uses_reference(params: list[str], returns: str) -> bool:
    for type_name in params + [returns]:
        for banned in NO_REFERENCE_TYPES:
            if banned in type_name:
                return False
    return True


def quote_param(param: str) -> str:
    if any(ch in param for ch in "[]<>,"):
        return '"' + param.replace('"', '\\"') + '"'
    return param


class _ForceQuotedDumper(yaml.SafeDumper):
    pass


def _represent_str(dumper: yaml.SafeDumper, data: str):
    style = '"' if (
        data == ""
        or data.lower() in {"true", "false", "null", "yes", "no", "on", "off"}
        or any(ch in data for ch in ":{}[]&*?|>%!@`\\#,")
        or data.startswith(".")
        or data.startswith("'")
        or (data[:1].isdigit() and not data.isdigit() and not re.fullmatch(r"-?\d+(\.\d+)?", data))
    ) else None
    return dumper.represent_scalar("tag:yaml.org,2002:str", data, style=style)


_ForceQuotedDumper.add_representer(str, _represent_str)


def flow_yaml(value: Any) -> str:
    return yaml.dump(
        value,
        Dumper=_ForceQuotedDumper,
        default_flow_style=True,
        allow_unicode=True,
        width=10_000,
    ).strip()


def dump_spec(spec: dict[str, Any]) -> str:
    lines = [
        f"className: {spec['className']}",
        f"method: {spec['method']}",
        "params: [" + ", ".join(quote_param(p) for p in spec["params"]) + "]",
        f"returns: {quote_param(spec['returns']) if any(ch in spec['returns'] for ch in '[]<>') else spec['returns']}",
    ]
    if "referenceClass" in spec:
        lines.append(f"referenceClass: {spec['referenceClass']}")
        lines.append(f"referenceMethod: {spec['referenceMethod']}")
        lines.append(f"referenceStatic: {str(spec['referenceStatic']).lower()}")
    if spec.get("helpers"):
        lines.append("helpers: [" + ", ".join(spec["helpers"]) + "]")
    lines.append("cases:")
    for case in spec["cases"]:
        lines.append(f"  - name: {case['name']}")
        lines.append(f"    args: {flow_yaml(case['args'])}")
        if "expected" in case:
            expected = case["expected"]
            if expected is None:
                lines.append("    expected: null")
            else:
                lines.append(f"    expected: {flow_yaml(expected)}")
    return "\n".join(lines) + "\n"


def build_spec(problem: dict[str, Any]) -> dict[str, Any] | None:
    problem_id = problem["id"]
    class_name = problem["className"]
    simple = class_name.split(".")[-1]
    src = source_path(class_name)
    if not src.exists():
        return None
    method_info = extract_primary_method(src.read_text(), simple)
    if method_info is None:
        return None
    returns, method, params, is_static = method_info

    # Skip unsupported exotic return/param types for the sandbox.
    unsupported = {"GraphNode", "BuiltQuery", "Optional<String>", "Map<String,Object>", "Map<String, Object>"}
    if returns in unsupported or any(p in unsupported for p in params):
        return None
    if "Map<" in returns or any("Map<" in p for p in params):
        return None

    cases: list[dict[str, Any]] = []
    test = test_path(class_name)
    if test.exists():
        cases = extract_cases_from_test(test.read_text(), simple, method)

    if not cases and problem_id in FALLBACK_CASES:
        cases = [dict(c) for c in FALLBACK_CASES[problem_id]]

    if not cases:
        return None

    # Validate arity
    filtered = []
    for case in cases:
        if len(case.get("args", [])) != len(params):
            continue
        filtered.append(case)
    if not filtered and problem_id in FALLBACK_CASES:
        filtered = [dict(c) for c in FALLBACK_CASES[problem_id]]
        filtered = [c for c in filtered if len(c.get("args", [])) == len(params)]
    if not filtered:
        return None

    can_ref = uses_reference(params, returns)
    if not can_ref and any("expected" not in c for c in filtered):
        fb = [dict(c) for c in FALLBACK_CASES.get(problem_id, []) if "expected" in c]
        fb = [c for c in fb if len(c.get("args", [])) == len(params)]
        if not fb:
            return None
        filtered = fb
    if returns == "void" and not can_ref and any("expected" not in c for c in filtered):
        return None

    helpers = needs_helper(params, returns)
    spec: dict[str, Any] = {
        "className": "Solution",
        "method": method,
        "params": params,
        "returns": returns,
        "cases": filtered,
    }
    if helpers:
        spec["helpers"] = helpers
    if can_ref:
        # Drop expected so reference computes it, unless void with provided expected.
        cleaned = []
        for case in filtered:
            item = {"name": case["name"], "args": case["args"]}
            if returns == "void" and "expected" in case:
                item["expected"] = case["expected"]
            cleaned.append(item)
        spec["cases"] = cleaned
        spec["referenceClass"] = class_name
        spec["referenceMethod"] = method
        spec["referenceStatic"] = is_static
    else:
        if any("expected" not in c for c in filtered):
            return None
    return spec


def main() -> int:
    SPECS_DIR.mkdir(parents=True, exist_ok=True)
    existing = {p.stem for p in SPECS_DIR.glob("*.yml")}
    problems = load_catalog()
    created = []
    skipped_stateful = []
    skipped_other = []
    for problem in problems:
        pid = problem["id"]
        if pid in existing:
            continue
        if pid in STATEFUL_SKIP:
            skipped_stateful.append(pid)
            continue
        try:
            spec = build_spec(problem)
        except Exception as exc:  # noqa: BLE001
            skipped_other.append(f"{pid} ({exc})")
            continue
        if spec is None:
            skipped_other.append(pid)
            continue
        path = SPECS_DIR / f"{pid}.yml"
        path.write_text(dump_spec(spec))
        created.append(pid)

    print(f"existing_before={len(existing)}")
    print(f"created={len(created)}")
    print(f"skipped_stateful={len(skipped_stateful)}")
    print(f"skipped_other={len(skipped_other)}")
    print("CREATED:")
    for pid in created:
        print(f"  {pid}")
    print("SKIPPED_STATEFUL:")
    for pid in skipped_stateful:
        print(f"  {pid}")
    print("SKIPPED_OTHER:")
    for pid in skipped_other:
        print(f"  {pid}")
    print(f"total_specs_now={len(list(SPECS_DIR.glob('*.yml')))}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
