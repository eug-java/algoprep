package com.algoprep.patterns.twoheaps;

import com.algoprep.common.Interval;
import java.util.PriorityQueue;

/** Finds the earliest interval beginning at or after each interval's end. */
public final class NextInterval {
  private NextInterval() {}
  public static int[] findNextInterval(Interval[] intervals) {
    if (intervals == null) return new int[0];
    int[] result = new int[intervals.length];
    PriorityQueue<Integer> starts = new PriorityQueue<>((a, b) -> Integer.compare(intervals[b].start(), intervals[a].start()));
    PriorityQueue<Integer> ends = new PriorityQueue<>((a, b) -> Integer.compare(intervals[b].end(), intervals[a].end()));
    for (int i = 0; i < intervals.length; i++) { starts.offer(i); ends.offer(i); }
    while (!ends.isEmpty()) {
      int index = ends.poll(); result[index] = -1;
      if (starts.peek() != null && intervals[starts.peek()].start() >= intervals[index].end()) {
        int candidate = starts.poll();
        while (!starts.isEmpty() && intervals[starts.peek()].start() >= intervals[index].end()) candidate = starts.poll();
        result[index] = candidate; starts.offer(candidate);
      }
    }
    return result;
  }
}
