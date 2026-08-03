package com.algoprep.patterns.kwaymerge;

import java.util.List;
import java.util.PriorityQueue;

/** Finds the shortest range containing a value from each sorted list. */
public final class SmallestRange {
  private SmallestRange() {}
  public static int[] smallestRange(List<List<Integer>> nums) {
    if (nums == null || nums.isEmpty() || nums.stream().anyMatch(List::isEmpty)) return new int[0];
    PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
    int max = Integer.MIN_VALUE, bestStart = 0, bestEnd = Integer.MAX_VALUE;
    for (int i = 0; i < nums.size(); i++) { int value = nums.get(i).getFirst(); heap.offer(new int[] {value, i, 0}); max = Math.max(max, value); }
    while (heap.size() == nums.size()) {
      int[] smallest = heap.poll();
      if (max - smallest[0] < bestEnd - bestStart) { bestStart = smallest[0]; bestEnd = max; }
      int next = smallest[2] + 1;
      if (next < nums.get(smallest[1]).size()) {
        int value = nums.get(smallest[1]).get(next);
        heap.offer(new int[] {value, smallest[1], next}); max = Math.max(max, value);
      }
    }
    return new int[] {bestStart, bestEnd};
  }
}
