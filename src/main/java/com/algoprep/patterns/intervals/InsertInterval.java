package com.algoprep.patterns.intervals;

import com.algoprep.common.Interval;
import java.util.*;

/** Interval insertion and merge; O(n) time and O(n) output space. */
public final class InsertInterval {
  private InsertInterval() {}

  public static List<Interval> insert(List<Interval> intervals, Interval newInterval) {
    if (newInterval == null) throw new IllegalArgumentException("newInterval is required");
    List<Interval> result = new ArrayList<>();
    if (intervals == null) return List.of(newInterval);
    int i = 0;
    while (i < intervals.size() && intervals.get(i).end() < newInterval.start())
      result.add(intervals.get(i++));
    int start = newInterval.start(), end = newInterval.end();
    while (i < intervals.size() && intervals.get(i).start() <= end) {
      Interval x = intervals.get(i++);
      start = Math.min(start, x.start());
      end = Math.max(end, x.end());
    }
    result.add(new Interval(start, end));
    while (i < intervals.size()) result.add(intervals.get(i++));
    return result;
  }
}
