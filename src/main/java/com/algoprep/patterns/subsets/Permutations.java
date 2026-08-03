package com.algoprep.patterns.subsets;

import java.util.ArrayList;
import java.util.List;

/** Generates all permutations of distinct input values. */
public final class Permutations {
  private Permutations() {}

  public static List<List<Integer>> findPermutations(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    result.add(new ArrayList<>());
    for (int num : nums) {
      List<List<Integer>> next = new ArrayList<>();
      for (List<Integer> permutation : result) {
        for (int i = 0; i <= permutation.size(); i++) {
          List<Integer> copy = new ArrayList<>(permutation);
          copy.add(i, num);
          next.add(copy);
        }
      }
      result = next;
    }
    return result;
  }
}
