package com.algoprep.patterns.topk;

import java.util.PriorityQueue;

/** Returns the kth largest number using a bounded min-heap. */
public final class KthLargest {
  private KthLargest() {}
  public static int findKthLargest(int[] nums, int k) {
    if (nums == null || k < 1 || k > nums.length) throw new IllegalArgumentException("k must index nums");
    PriorityQueue<Integer> heap = new PriorityQueue<>();
    for (int value : nums) { heap.offer(value); if (heap.size() > k) heap.poll(); }
    return heap.peek();
  }
}
