package com.algoprep.patterns.matrices;

/** Rotates a square matrix 90 degrees clockwise in place. */
public final class RotateImage {
  private RotateImage() {}

  public static void rotate(int[][] matrix) {
    int n = matrix.length;
    for (int layer = 0; layer < n / 2; layer++) {
      int first = layer, last = n - 1 - layer;
      for (int i = first; i < last; i++) {
        int offset = i - first;
        int top = matrix[first][i];
        matrix[first][i] = matrix[last - offset][first];
        matrix[last - offset][first] = matrix[last][last - offset];
        matrix[last][last - offset] = matrix[i][last];
        matrix[i][last] = top;
      }
    }
  }
}
