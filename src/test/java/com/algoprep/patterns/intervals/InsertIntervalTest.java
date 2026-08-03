package com.algoprep.patterns.intervals;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.algoprep.common.Interval;
import java.util.List;
import org.junit.jupiter.api.Test;

class InsertIntervalTest {

  @Test
  void insertsAndMergesNeighbors() {
    // [1,3] + [2,6] + [5,7] → [1,7], then [8,9]
    assertEquals(
        List.of(new Interval(1, 7), new Interval(8, 9)),
        InsertInterval.insert(
            List.of(new Interval(1, 3), new Interval(5, 7), new Interval(8, 9)),
            new Interval(2, 6)));
  }

  @Test
  void insertsIntoEmpty() {
    assertEquals(List.of(new Interval(1, 2)), InsertInterval.insert(List.of(), new Interval(1, 2)));
  }
}
