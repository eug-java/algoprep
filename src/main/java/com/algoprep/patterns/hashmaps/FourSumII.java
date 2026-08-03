package com.algoprep.patterns.hashmaps;

import java.util.HashMap;
import java.util.Map;

/** Counts zero-sum tuples formed by four integer arrays. */
public final class FourSumII {
  private FourSumII() {}
  public static int fourSumCount(int[] nums1, int[] nums2, int[] nums3, int[] nums4) {
    Map<Integer,Integer> pairs = new HashMap<>();
    for (int a:nums1) for (int b:nums2) pairs.merge(a+b,1,Integer::sum);
    int count=0; for (int c:nums3) for (int d:nums4) count += pairs.getOrDefault(-c-d,0); return count;
  }
}
