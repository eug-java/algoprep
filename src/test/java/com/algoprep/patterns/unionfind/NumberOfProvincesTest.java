package com.algoprep.patterns.unionfind;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NumberOfProvincesTest {
  @Test
  void countsDisconnectedGroups() {
    assertEquals(2, NumberOfProvinces.findCircleNum(new int[][] {{1, 1, 0}, {1, 1, 0}, {0, 0, 1}}));
  }
}
