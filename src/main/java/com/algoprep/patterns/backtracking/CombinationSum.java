package com.algoprep.patterns.backtracking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Finds combinations that sum to a target, allowing candidate reuse. */
public final class CombinationSum {
  private CombinationSum() {}

  public static List<List<Integer>> findCombinations(int[] candidates, int target) {
    Arrays.sort(candidates);
    List<List<Integer>> result = new ArrayList<>();
    search(candidates, target, 0, new ArrayList<>(), result);
    return result;
  }

  private static void search(
      int[] candidates,
      int remaining,
      int start,
      List<Integer> current,
      List<List<Integer>> result) {
    if (remaining == 0) {
      result.add(new ArrayList<>(current));
      return;
    }
    for (int i = start; i < candidates.length && candidates[i] <= remaining; i++) {
      if (i > start && candidates[i] == candidates[i - 1]) continue;
      current.add(candidates[i]);
      search(candidates, remaining - candidates[i], i, current, result);
      current.removeLast();
    }
  }
}
