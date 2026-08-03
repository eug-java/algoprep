package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.algoprep.common.Interval;
import java.util.List;
import org.junit.jupiter.api.Test;

class EmployeeFreeTimeTest {
  @Test
  void returnsOnlyFiniteCommonGaps() {
    List<List<Interval>> schedule =
        List.of(
            List.of(new Interval(1, 3), new Interval(6, 7)),
            List.of(new Interval(2, 4)),
            List.of(new Interval(2, 5), new Interval(9, 12)));

    assertEquals(
        List.of(new Interval(5, 6), new Interval(7, 9)),
        EmployeeFreeTime.employeeFreeTime(schedule));
  }

  @Test
  void mergesOverlappingAndTouchingBusyTime() {
    assertTrue(
        EmployeeFreeTime.employeeFreeTime(
                List.of(List.of(new Interval(1, 3)), List.of(new Interval(3, 5))))
            .isEmpty());
    assertTrue(EmployeeFreeTime.employeeFreeTime(List.of()).isEmpty());
  }
}
