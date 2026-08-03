package com.algoprep.patterns.matrices;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MatrixDiagonalSumTest {
  @Test void excludesDuplicatedCenterValue() {
    assertEquals(25, MatrixDiagonalSum.diagonalSum(new int[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}));
  }

  @Test void sumsBothDiagonalsOfEvenMatrix() {
    assertEquals(68, MatrixDiagonalSum.diagonalSum(new int[][] {{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}, {13, 14, 15, 16}}));
  }
}
