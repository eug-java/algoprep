package com.algoprep.patterns.matrices;

/** Searches a matrix sorted left-to-right and top-to-bottom. */
public final class Search2DMatrix {
  private Search2DMatrix() {}
  public static boolean searchMatrix(int[][] matrix, int target) { if (matrix == null || matrix.length == 0 || matrix[0].length == 0) return false; int row = 0, col = matrix[0].length - 1; while (row < matrix.length && col >= 0) { int value = matrix[row][col]; if (value == target) return true; if (value > target) col--; else row++; } return false; }
}
