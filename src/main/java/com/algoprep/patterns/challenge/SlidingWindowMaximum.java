package com.algoprep.patterns.challenge;

import java.util.ArrayDeque;
import java.util.Deque;

/** Returns max of every contiguous window of size k using a monotonic deque. */
public final class SlidingWindowMaximum {
  private SlidingWindowMaximum() {}

  public static int[] maxSlidingWindow(int[] nums, int k) {
    if (nums == null || nums.length == 0 || k <= 0) return new int[0];
    if (k == 1) return nums.clone();
    int n = nums.length;
    int[] out = new int[n - k + 1];
    Deque<Integer> dq = new ArrayDeque<>();
    for (int i = 0; i < n; i++) {
      while (!dq.isEmpty() && dq.peekFirst() <= i - k) dq.pollFirst();
      while (!dq.isEmpty() && nums[dq.peekLast()] <= nums[i]) dq.pollLast();
      dq.addLast(i);
      if (i >= k - 1) out[i - k + 1] = nums[dq.peekFirst()];
    }
    return out;
  }
}
