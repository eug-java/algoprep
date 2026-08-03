package com.algoprep.patterns.topk;

import java.util.*;

/** Max-heap selection; O(n log k) time and O(k) space. */
public final class KClosestPoints {
  private KClosestPoints() {}

  public static int[][] findClosestPoints(int[][] points, int k) {
    if (points == null || k < 0 || k > points.length)
      throw new IllegalArgumentException("invalid k");
    PriorityQueue<int[]> heap =
        new PriorityQueue<>((a, b) -> Long.compare(distance(b), distance(a)));
    for (int[] p : points) {
      if (p == null || p.length < 2)
        throw new IllegalArgumentException("each point needs two coordinates");
      heap.offer(p);
      if (heap.size() > k) heap.poll();
    }
    int[][] result = new int[k][];
    for (int i = k - 1; i >= 0; i--) result[i] = heap.poll();
    return result;
  }

  private static long distance(int[] p) {
    return (long) p[0] * p[0] + (long) p[1] * p[1];
  }
}
