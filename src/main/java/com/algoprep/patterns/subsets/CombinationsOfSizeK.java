package com.algoprep.patterns.subsets;

import java.util.ArrayList;
import java.util.List;

/** Generates combinations of k numbers selected from the inclusive range 1..n. */
public final class CombinationsOfSizeK {
  private CombinationsOfSizeK() {}

  public static List<List<Integer>> combine(int n, int k) {
    List<List<Integer>> combinations = new ArrayList<>();
    if (k < 0 || k > n) return combinations;
    backtrack(1, n, k, new ArrayList<>(), combinations);
    return combinations;
  }

  private static void backtrack(
      int start, int n, int remaining, List<Integer> current, List<List<Integer>> combinations) {
    if (remaining == 0) {
      combinations.add(new ArrayList<>(current));
      return;
    }
    for (int value = start; value <= n - remaining + 1; value++) {
      current.add(value);
      backtrack(value + 1, n, remaining - 1, current, combinations);
      current.removeLast();
    }
  }
}
