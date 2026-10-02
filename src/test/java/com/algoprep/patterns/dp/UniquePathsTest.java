package com.algoprep.patterns.dp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UniquePathsTest {
  @Test
  void countsPaths() {
    assertEquals(28, UniquePaths.uniquePaths(3, 7));
    assertEquals(3, UniquePaths.uniquePaths(3, 2));
    assertEquals(1, UniquePaths.uniquePaths(1, 1));
  }
}
