package com.algoprep.patterns.matrices;

/** Sets rows and columns to zero when any of their cells is zero. */
public final class SetMatrixZeroes {
  private SetMatrixZeroes() {}

  public static void setZeroes(int[][] matrix) {
    boolean firstRowZero = false, firstColumnZero = false;
    for (int col = 0; col < matrix[0].length; col++) firstRowZero |= matrix[0][col] == 0;
    for (int row = 0; row < matrix.length; row++) firstColumnZero |= matrix[row][0] == 0;
    for (int row = 1; row < matrix.length; row++) {
      for (int col = 1; col < matrix[0].length; col++) {
        if (matrix[row][col] == 0) {
          matrix[row][0] = 0;
          matrix[0][col] = 0;
        }
      }
    }
    for (int row = 1; row < matrix.length; row++)
      for (int col = 1; col < matrix[0].length; col++)
        if (matrix[row][0] == 0 || matrix[0][col] == 0) matrix[row][col] = 0;
    if (firstRowZero) for (int col = 0; col < matrix[0].length; col++) matrix[0][col] = 0;
    if (firstColumnZero) for (int row = 0; row < matrix.length; row++) matrix[row][0] = 0;
  }
}
