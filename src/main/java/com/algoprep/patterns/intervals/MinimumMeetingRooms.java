package com.algoprep.patterns.intervals;

import com.algoprep.common.Interval;
import java.util.Arrays;
import java.util.PriorityQueue;

/** Calculates the maximum number of simultaneously active meetings. */
public final class MinimumMeetingRooms {
  private MinimumMeetingRooms() {}
  public static int minMeetingRooms(Interval[] intervals) {
    if (intervals == null || intervals.length == 0) return 0;
    Arrays.sort(intervals);
    PriorityQueue<Integer> endings = new PriorityQueue<>();
    int rooms = 0;
    for (Interval interval : intervals) {
      while (!endings.isEmpty() && endings.peek() <= interval.start()) endings.poll();
      endings.offer(interval.end());
      rooms = Math.max(rooms, endings.size());
    }
    return rooms;
  }
}
