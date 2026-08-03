package com.algoprep.patterns.subsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Generates unique subsets when input values may repeat. */
public final class SubsetsWithDuplicates {
  private SubsetsWithDuplicates() {}

  public static List<List<Integer>> findSubsets(int[] nums) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    result.add(new ArrayList<>());
    int start = 0;
    int end = 0;
    for (int i = 0; i < nums.length; i++) {
      start = i > 0 && nums[i] == nums[i - 1] ? end + 1 : 0;
      end = result.size() - 1;
      int size = result.size();
      for (int j = start; j < size; j++) {
        List<Integer> subset = new ArrayList<>(result.get(j));
        subset.add(nums[i]);
        result.add(subset);
      }
    }
    return result;
  }
}
