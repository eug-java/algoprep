package com.algoprep.patterns.intervals;

import com.algoprep.common.Interval;
import java.util.*;

/** Interval merging; O(n log n) time and O(n) output space. */
public final class MergeIntervals {
  private MergeIntervals() {}

  public static List<Interval> merge(List<Interval> intervals) {
    if (intervals == null || intervals.isEmpty()) return List.of();
    List<Interval> sorted = new ArrayList<>(intervals);
    sorted.sort(Comparator.naturalOrder());
    List<Interval> result = new ArrayList<>();
    Interval current = sorted.getFirst();
    for (int i = 1; i < sorted.size(); i++) {
      Interval next = sorted.get(i);
      if (next.start() <= current.end())
        current = new Interval(current.start(), Math.max(current.end(), next.end()));
      else {
        result.add(current);
        current = next;
      }
    }
    result.add(current);
    return result;
  }
}
