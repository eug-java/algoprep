package com.algoprep.patterns.hashmaps;

import java.util.HashMap;
import java.util.Map;

/** Counts contiguous subarrays that have a target sum. */
public final class SubarraySumEqualsK {
  private SubarraySumEqualsK() {}

  public static int findSubarrayCount(int[] nums, int k) {
    Map<Integer, Integer> frequencies = new HashMap<>();
    frequencies.put(0, 1);
    int sum = 0, count = 0;
    for (int n : nums) {
      sum += n;
      count += frequencies.getOrDefault(sum - k, 0);
      frequencies.merge(sum, 1, Integer::sum);
    }
    return count;
  }
}
