package com.algoprep.patterns.hashmaps;

import java.util.HashMap;
import java.util.Map;

/** Finds the longest subarray with equally many zeroes and ones. */
public final class ContiguousArray {
  private ContiguousArray() {}
  public static int findMaxLength(int[] nums) {
    Map<Integer,Integer> first = new HashMap<>(); first.put(0, -1); int sum=0, best=0;
    for (int i=0;i<nums.length;i++) { sum += nums[i] == 0 ? -1 : 1; Integer start=first.putIfAbsent(sum,i); if (start != null) best=Math.max(best,i-start); }
    return best;
  }
}
