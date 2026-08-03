package com.algoprep.patterns.matrices;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class RotateImageTest {
  @Test
  void rotatesSquareMatrixClockwiseInPlace() {
    int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
    RotateImage.rotate(matrix);
    assertArrayEquals(new int[] {7, 4, 1}, matrix[0]);
    assertArrayEquals(new int[] {8, 5, 2}, matrix[1]);
    assertArrayEquals(new int[] {9, 6, 3}, matrix[2]);
  }

  @Test
  void leavesSingleCellMatrixUntouched() {
    int[][] matrix = {{42}};
    RotateImage.rotate(matrix);
    assertArrayEquals(new int[] {42}, matrix[0]);
  }
}
