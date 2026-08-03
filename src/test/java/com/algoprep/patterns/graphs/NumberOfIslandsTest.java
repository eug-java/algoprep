package com.algoprep.patterns.graphs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NumberOfIslandsTest {
  @Test
  void countsSeparatedIslands() {
    char[][] grid = {{'1', '1', '0', '0'}, {'1', '0', '0', '1'}, {'0', '0', '1', '1'}};
    assertEquals(2, NumberOfIslands.countIslands(grid));
  }

  @Test
  void handlesEmptyGrid() {
    assertEquals(0, NumberOfIslands.countIslands(new char[0][]));
  }
}
