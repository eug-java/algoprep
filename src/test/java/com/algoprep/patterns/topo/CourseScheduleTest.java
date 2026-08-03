package com.algoprep.patterns.topo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CourseScheduleTest {
  @Test
  void detectsCyclesInPrerequisites() {
    assertTrue(CourseSchedule.canFinish(2, new int[][] {{1, 0}}));
    assertFalse(CourseSchedule.canFinish(2, new int[][] {{1, 0}, {0, 1}}));
    assertTrue(CourseSchedule.canFinish(3, new int[][] {}));
  }
}
