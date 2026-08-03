package com.algoprep.patterns.twoheaps;

import java.util.Collections;
import java.util.PriorityQueue;

/** Computes a median for every contiguous window. */
public final class SlidingWindowMedian {
  private SlidingWindowMedian() {}
  public static double[] medianSlidingWindow(int[] nums, int k) {
    if (nums == null || k <= 0 || k > nums.length) return new double[0];
    PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder());
    PriorityQueue<Integer> high = new PriorityQueue<>();
    double[] result = new double[nums.length - k + 1];
    for (int i = 0; i < nums.length; i++) {
      if (low.isEmpty() || nums[i] <= low.peek()) low.offer(nums[i]); else high.offer(nums[i]);
      balance(low, high);
      if (i >= k) {
        if (nums[i - k] <= low.peek()) low.remove(nums[i - k]); else high.remove(nums[i - k]);
        balance(low, high);
      }
      if (i >= k - 1) result[i - k + 1] = low.size() == high.size()
          ? ((double) low.peek() + high.peek()) / 2 : low.peek();
    }
    return result;
  }
  private static void balance(PriorityQueue<Integer> low, PriorityQueue<Integer> high) {
    if (low.size() > high.size() + 1) high.offer(low.poll());
    else if (low.size() < high.size()) low.offer(high.poll());
  }
}
