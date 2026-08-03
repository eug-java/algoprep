package com.algoprep.patterns.topk;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/** Rearranges characters so adjacent characters differ, when possible. */
public final class ReorganizeString {
  private ReorganizeString() {}
  public static String reorganizeString(String s) {
    if (s == null || s.isEmpty()) return "";
    Map<Character, Integer> counts = new HashMap<>();
    for (char c : s.toCharArray()) counts.merge(c, 1, Integer::sum);
    PriorityQueue<Map.Entry<Character, Integer>> heap = new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());
    heap.addAll(counts.entrySet());
    StringBuilder result = new StringBuilder();
    Map.Entry<Character, Integer> previous = null;
    while (!heap.isEmpty()) {
      Map.Entry<Character, Integer> current = heap.poll();
      result.append(current.getKey()); current.setValue(current.getValue() - 1);
      if (previous != null && previous.getValue() > 0) heap.offer(previous);
      previous = current;
    }
    return result.length() == s.length() ? result.toString() : "";
  }
}
