package com.algoprep.patterns.matrices;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class SetMatrixZeroesTest {
  @Test
  void zeroesAffectedRowsAndColumns() {
    int[][] matrix = {{1, 1, 1}, {1, 0, 1}, {1, 1, 1}};
    SetMatrixZeroes.setZeroes(matrix);
    assertArrayEquals(new int[] {1, 0, 1}, matrix[0]);
    assertArrayEquals(new int[] {0, 0, 0}, matrix[1]);
    assertArrayEquals(new int[] {1, 0, 1}, matrix[2]);
  }

  @Test
  void handlesFirstRowAndColumnMarkers() {
    int[][] matrix = {{0, 1}, {1, 1}};
    SetMatrixZeroes.setZeroes(matrix);
    assertArrayEquals(new int[] {0, 0}, matrix[0]);
    assertArrayEquals(new int[] {0, 1}, matrix[1]);
  }
}
