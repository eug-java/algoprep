package com.algoprep.patterns.unionfind;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class RedundantConnectionTest {
  @Test
  void findsCycleClosingEdge() {
    assertArrayEquals(
        new int[] {2, 3},
        RedundantConnection.findRedundantConnection(new int[][] {{1, 2}, {1, 3}, {2, 3}}));
  }
}
