package com.algoprep.patterns.intervals;

import com.algoprep.common.Interval;
import java.util.ArrayList;
import java.util.List;

/** Intersects two sorted, non-overlapping interval lists. */
public final class IntervalIntersection {
  private IntervalIntersection() {}
  public static List<Interval> intervalIntersection(List<Interval> a, List<Interval> b) {
    List<Interval> result = new ArrayList<>();
    if (a == null || b == null) return result;
    int i = 0, j = 0;
    while (i < a.size() && j < b.size()) {
      Interval first = a.get(i), second = b.get(j);
      int start = Math.max(first.start(), second.start()), end = Math.min(first.end(), second.end());
      if (start <= end) result.add(new Interval(start, end));
      if (first.end() < second.end()) i++; else j++;
    }
    return result;
  }
}
