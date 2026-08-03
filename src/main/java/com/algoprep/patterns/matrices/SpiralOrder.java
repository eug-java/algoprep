package com.algoprep.patterns.matrices;

import java.util.ArrayList;
import java.util.List;

/** Traverses a matrix in clockwise spiral order. */
public final class SpiralOrder {
  private SpiralOrder() {}

  public static List<Integer> spiralOrder(int[][] matrix) {
    List<Integer> result = new ArrayList<>();
    if (matrix.length == 0 || matrix[0].length == 0) return result;
    int top = 0, bottom = matrix.length - 1, left = 0, right = matrix[0].length - 1;
    while (top <= bottom && left <= right) {
      for (int col = left; col <= right; col++) result.add(matrix[top][col]);
      top++;
      for (int row = top; row <= bottom; row++) result.add(matrix[row][right]);
      right--;
      if (top <= bottom) for (int col = right; col >= left; col--) result.add(matrix[bottom][col]);
      bottom--;
      if (left <= right) for (int row = bottom; row >= top; row--) result.add(matrix[row][left]);
      left++;
    }
    return result;
  }
}
