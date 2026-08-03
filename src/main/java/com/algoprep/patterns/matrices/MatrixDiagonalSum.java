package com.algoprep.patterns.matrices;

/** Sums both diagonals of a square matrix without double-counting the center. */
public final class MatrixDiagonalSum {
  private MatrixDiagonalSum() {}

  public static int diagonalSum(int[][] mat) {
    if (mat == null) throw new IllegalArgumentException("matrix must not be null");
    int sum = 0;
    for (int row = 0; row < mat.length; row++) {
      if (mat[row] == null || mat[row].length != mat.length) {
        throw new IllegalArgumentException("matrix must be square");
      }
      sum += mat[row][row] + mat[row][mat.length - 1 - row];
    }
    if (mat.length % 2 == 1) sum -= mat[mat.length / 2][mat.length / 2];
    return sum;
  }
}
