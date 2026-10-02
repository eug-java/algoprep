package com.algoprep.patterns.graphs;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IsGraphBipartiteTest {
  @Test
  void colorsAnEvenCycle() {
    assertTrue(IsGraphBipartite.isBipartite(new int[][] {{1, 3}, {0, 2}, {1, 3}, {0, 2}}));
  }

  @Test
  void rejectsAnOddCycle() {
    assertFalse(IsGraphBipartite.isBipartite(new int[][] {{1, 2, 3}, {0, 2}, {0, 1, 3}, {0, 2}}));
  }
}
