package com.algoprep.patterns.topo;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class CourseScheduleOrderTest {
  @Test
  void returnsTopologicalOrderOrEmptyWhenCyclic() {
    assertArrayEquals(
        new int[] {0, 1, 2, 3},
        CourseScheduleOrder.findOrder(4, new int[][] {{1, 0}, {2, 0}, {3, 1}, {3, 2}}));
    assertArrayEquals(new int[0], CourseScheduleOrder.findOrder(2, new int[][] {{1, 0}, {0, 1}}));
  }
}
