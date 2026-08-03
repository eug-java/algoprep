package com.algoprep.patterns.dp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ClimbingStairsTest {
  @Test
  void countsFibonacciStylePossibilities() {
    assertEquals(1, ClimbingStairs.countWays(0));
    assertEquals(1, ClimbingStairs.countWays(1));
    assertEquals(8, ClimbingStairs.countWays(5));
    assertEquals(0, ClimbingStairs.countWays(-1));
  }
}
