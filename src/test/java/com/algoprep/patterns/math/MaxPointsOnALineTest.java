package com.algoprep.patterns.math;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MaxPointsOnALineTest {
  @Test void findsMostFrequentNormalizedSlope() {
    assertEquals(4, MaxPointsOnALine.maxPoints(new int[][] {{1, 1}, {2, 2}, {3, 3}, {4, 4}, {1, 2}}));
  }

  @Test void handlesVerticalLinesAndDuplicates() {
    assertEquals(4, MaxPointsOnALine.maxPoints(new int[][] {{2, 1}, {2, 2}, {2, 1}, {2, 3}, {0, 0}}));
  }

  @Test void handlesEmptyAndSingletonInput() {
    assertEquals(0, MaxPointsOnALine.maxPoints(new int[][] {}));
    assertEquals(1, MaxPointsOnALine.maxPoints(new int[][] {{5, -1}}));
  }
}
