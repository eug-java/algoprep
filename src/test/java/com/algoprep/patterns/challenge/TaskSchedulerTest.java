package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TaskSchedulerTest {
  @Test
  void schedulesWithCooling() {
    assertEquals(8, TaskScheduler.leastInterval(new char[] {'A', 'A', 'A', 'B', 'B', 'B'}, 2));
    assertEquals(6, TaskScheduler.leastInterval(new char[] {'A', 'A', 'A', 'B', 'B', 'B'}, 0));
    assertEquals(10, TaskScheduler.leastInterval(new char[] {'A', 'A', 'A', 'A', 'B', 'B', 'C', 'C', 'D', 'D'}, 2));
  }
}
