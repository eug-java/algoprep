package com.algoprep.patterns.intervals;

import com.algoprep.common.Interval;
import java.util.*;

/** Interval overlap detection; O(n log n) time and O(n) space. */
public final class ConflictingAppointments {
  private ConflictingAppointments() {}

  public static boolean canAttendAllAppointments(Interval[] intervals) {
    if (intervals == null || intervals.length < 2) return true;
    Interval[] sorted = intervals.clone();
    Arrays.sort(sorted);
    for (int i = 1; i < sorted.length; i++)
      if (sorted[i].start() < sorted[i - 1].end()) return false;
    return true;
  }
}
