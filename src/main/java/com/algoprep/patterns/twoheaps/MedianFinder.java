package com.algoprep.patterns.twoheaps;

import java.util.*;

/** Two heaps; O(log n) insertion and O(1) median lookup. */
public final class MedianFinder {
  private final PriorityQueue<Integer> lower = new PriorityQueue<>(Comparator.reverseOrder());
  private final PriorityQueue<Integer> upper = new PriorityQueue<>();

  public void addNum(int num) {
    if (lower.isEmpty() || num <= lower.peek()) lower.offer(num);
    else upper.offer(num);
    if (lower.size() > upper.size() + 1) upper.offer(lower.poll());
    else if (upper.size() > lower.size()) lower.offer(upper.poll());
  }

  public double findMedian() {
    if (lower.isEmpty()) throw new NoSuchElementException("no numbers added");
    return lower.size() == upper.size() ? ((double) lower.peek() + upper.peek()) / 2 : lower.peek();
  }
}
