package com.algoprep.patterns.intervals;

import static org.junit.jupiter.api.Assertions.*;

import com.algoprep.common.Interval;
import java.util.*;
import org.junit.jupiter.api.Test;

class MergeIntervalsTest {
  @Test
  void mergesUnsortedAndTouchingIntervals() {
    assertEquals(
        List.of(new Interval(1, 5), new Interval(7, 9)),
        MergeIntervals.merge(List.of(new Interval(2, 5), new Interval(1, 3), new Interval(7, 9))));
  }

  @Test
  void handlesEmpty() {
    assertTrue(MergeIntervals.merge(List.of()).isEmpty());
  }
}
