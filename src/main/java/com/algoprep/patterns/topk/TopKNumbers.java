package com.algoprep.patterns.topk;

import java.util.*;

/** Min-heap selection; O(n log k) time and O(k) space. */
public final class TopKNumbers {
  private TopKNumbers() {}

  public static List<Integer> findKLargestNumbers(int[] nums, int k) {
    if (nums == null || k < 0 || k > nums.length) throw new IllegalArgumentException("invalid k");
    PriorityQueue<Integer> heap = new PriorityQueue<>();
    for (int num : nums) {
      heap.offer(num);
      if (heap.size() > k) heap.poll();
    }
    List<Integer> result = new ArrayList<>(heap);
    result.sort(Comparator.reverseOrder());
    return result;
  }
}
