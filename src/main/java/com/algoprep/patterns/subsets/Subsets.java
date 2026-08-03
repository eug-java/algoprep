package com.algoprep.patterns.subsets;

import java.util.ArrayList;
import java.util.List;

/** Generates every subset of distinct input values. */
public final class Subsets {
  private Subsets() {}

  public static List<List<Integer>> findSubsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    result.add(new ArrayList<>());
    for (int num : nums) {
      int size = result.size();
      for (int i = 0; i < size; i++) {
        List<Integer> subset = new ArrayList<>(result.get(i));
        subset.add(num);
        result.add(subset);
      }
    }
    return result;
  }
}
