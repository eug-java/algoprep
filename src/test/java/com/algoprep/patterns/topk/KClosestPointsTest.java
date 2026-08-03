package com.algoprep.patterns.topk;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class KClosestPointsTest {
  @Test
  void returnsClosestPoints() {
    assertArrayEquals(
        new int[] {0, 1},
        KClosestPoints.findClosestPoints(new int[][] {{1, 3}, {-2, 2}, {0, 1}}, 2)[0]);
  }

  @Test
  void handlesZero() {
    assertEquals(0, KClosestPoints.findClosestPoints(new int[][] {{1, 1}}, 0).length);
  }
}
