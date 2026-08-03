package com.algoprep.patterns.twoheaps;

import java.util.Collections;
import java.util.PriorityQueue;

/** Computes the median of a fixed, unsorted array using two balanced heaps. */
public final class StaticMedian {
  private StaticMedian() {}

  public static double findMedian(int[] nums) {
    if (nums == null || nums.length == 0) {
      throw new IllegalArgumentException("nums must not be null or empty");
    }

    PriorityQueue<Integer> lower = new PriorityQueue<>(Collections.reverseOrder());
    PriorityQueue<Integer> upper = new PriorityQueue<>();
    for (int value : nums) {
      if (lower.isEmpty() || value <= lower.peek()) lower.offer(value);
      else upper.offer(value);

      if (lower.size() > upper.size() + 1) upper.offer(lower.poll());
      else if (upper.size() > lower.size()) lower.offer(upper.poll());
    }
    return lower.size() == upper.size()
        ? ((double) lower.peek() + upper.peek()) / 2
        : lower.peek();
  }
}
