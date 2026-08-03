package com.algoprep.patterns.challenge;

import com.algoprep.common.Interval;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Finds finite intervals during which every employee is free. */
public final class EmployeeFreeTime {
  private EmployeeFreeTime() {}

  public static List<Interval> employeeFreeTime(List<List<Interval>> schedule) {
    if (schedule == null || schedule.isEmpty()) {
      return List.of();
    }

    List<Interval> busy = new ArrayList<>();
    for (List<Interval> employee : schedule) {
      if (employee != null) {
        busy.addAll(employee);
      }
    }
    if (busy.isEmpty()) {
      return List.of();
    }

    busy.sort(Comparator.naturalOrder());
    List<Interval> free = new ArrayList<>();
    int currentEnd = busy.getFirst().end();

    for (int i = 1; i < busy.size(); i++) {
      Interval interval = busy.get(i);
      if (interval.start() > currentEnd) {
        free.add(new Interval(currentEnd, interval.start()));
      }
      currentEnd = Math.max(currentEnd, interval.end());
    }
    return free;
  }
}
