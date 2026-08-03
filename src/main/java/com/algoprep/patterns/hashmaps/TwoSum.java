package com.algoprep.patterns.hashmaps;

import java.util.HashMap;
import java.util.Map;

/** Finds indices of two numbers that sum to a target. */
public final class TwoSum {
  private TwoSum() {}

  public static int[] findPair(int[] nums, int target) {
    Map<Integer, Integer> seen = new HashMap<>();
    for (int i = 0; i < nums.length; i++) {
      Integer prior = seen.get(target - nums[i]);
      if (prior != null) return new int[] {prior, i};
      seen.put(nums[i], i);
    }
    return new int[0];
  }
}
