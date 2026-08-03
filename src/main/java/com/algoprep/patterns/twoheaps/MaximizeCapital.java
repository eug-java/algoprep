package com.algoprep.patterns.twoheaps;

import java.util.*;

/** Two heaps for feasible projects; O((n+k) log n) time and O(n) space. */
public final class MaximizeCapital {
  private MaximizeCapital() {}

  public static int findMaximumCapital(int[] capital, int[] profits, int n, int initialCapital) {
    if (capital == null || profits == null || capital.length != profits.length || n < 0)
      throw new IllegalArgumentException("invalid projects");
    PriorityQueue<Integer> byCapital =
        new PriorityQueue<>(Comparator.comparingInt(i -> capital[i]));
    PriorityQueue<Integer> byProfit =
        new PriorityQueue<>((a, b) -> Integer.compare(profits[b], profits[a]));
    for (int i = 0; i < capital.length; i++) byCapital.offer(i);
    int available = initialCapital;
    for (int i = 0; i < n; i++) {
      while (!byCapital.isEmpty() && capital[byCapital.peek()] <= available)
        byProfit.offer(byCapital.poll());
      if (byProfit.isEmpty()) break;
      available += profits[byProfit.poll()];
    }
    return available;
  }
}
