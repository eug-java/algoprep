package com.algoprep.patterns.topk;

import java.util.*;

/** Min-heap by frequency; O(n log k) time and O(n) space. */
public final class TopKFrequent {
  private TopKFrequent() {}

  public static List<Integer> findTopKFrequentNumbers(int[] nums, int k) {
    if (nums == null || k < 0) throw new IllegalArgumentException("invalid input");
    Map<Integer, Integer> frequency = new HashMap<>();
    for (int n : nums) frequency.merge(n, 1, Integer::sum);
    if (k > frequency.size()) throw new IllegalArgumentException("k exceeds distinct count");
    PriorityQueue<Map.Entry<Integer, Integer>> heap =
        new PriorityQueue<>(
            Comparator.comparingInt(Map.Entry<Integer, Integer>::getValue)
                .thenComparing(Map.Entry::getKey));
    for (var entry : frequency.entrySet()) {
      heap.offer(entry);
      if (heap.size() > k) heap.poll();
    }
    List<Integer> result = new ArrayList<>();
    while (!heap.isEmpty()) result.add(heap.poll().getKey());
    result.sort(
        (a, b) -> {
          int byFrequency = Integer.compare(frequency.get(b), frequency.get(a));
          return byFrequency != 0 ? byFrequency : Integer.compare(b, a);
        });
    return result;
  }
}
