package com.algoprep.patterns.kwaymerge;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/** Returns k pairs with the smallest sums from two sorted arrays. */
public final class KSmallestPairs {
  private KSmallestPairs() {}
  public static List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
    List<List<Integer>> result = new ArrayList<>();
    if (nums1 == null || nums2 == null || nums1.length == 0 || nums2.length == 0 || k <= 0) return result;
    PriorityQueue<int[]> pairs = new PriorityQueue<>((a, b) -> Long.compare((long) nums1[a[0]] + nums2[a[1]], (long) nums1[b[0]] + nums2[b[1]]));
    for (int i = 0; i < Math.min(k, nums1.length); i++) pairs.offer(new int[] {i, 0});
    while (!pairs.isEmpty() && result.size() < k) {
      int[] pair = pairs.poll();
      result.add(List.of(nums1[pair[0]], nums2[pair[1]]));
      if (pair[1] + 1 < nums2.length) pairs.offer(new int[] {pair[0], pair[1] + 1});
    }
    return result;
  }
}
