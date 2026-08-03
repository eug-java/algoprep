package com.algoprep.patterns.subsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Generates each distinct permutation of an input array. */
public final class UniquePermutations {
  private UniquePermutations() {}
  public static List<List<Integer>> permuteUnique(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    if (nums == null) return result;
    int[] sorted = nums.clone(); Arrays.sort(sorted);
    backtrack(sorted, new boolean[sorted.length], new ArrayList<>(), result);
    return result;
  }
  private static void backtrack(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> result) {
    if (path.size() == nums.length) { result.add(new ArrayList<>(path)); return; }
    for (int i = 0; i < nums.length; i++) {
      if (used[i] || (i > 0 && nums[i] == nums[i - 1] && !used[i - 1])) continue;
      used[i] = true; path.add(nums[i]); backtrack(nums, used, path, result); path.remove(path.size() - 1); used[i] = false;
    }
  }
}
