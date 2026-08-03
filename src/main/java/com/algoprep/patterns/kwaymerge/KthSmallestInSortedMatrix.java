package com.algoprep.patterns.kwaymerge;

import java.util.*;

/** K-way merge across sorted rows; O(k log r) time and O(r) space. */
public final class KthSmallestInSortedMatrix {
  private KthSmallestInSortedMatrix() {}

  private record Cell(int value, int row, int col) {}

  public static int findKthSmallest(int[][] matrix, int k) {
    if (matrix == null || k < 1) throw new IllegalArgumentException("invalid matrix or k");
    PriorityQueue<Cell> heap = new PriorityQueue<>(Comparator.comparingInt(Cell::value));
    int total = 0;
    for (int r = 0; r < matrix.length; r++) {
      total += matrix[r].length;
      if (matrix[r].length > 0) heap.offer(new Cell(matrix[r][0], r, 0));
    }
    if (k > total) throw new IllegalArgumentException("k exceeds element count");
    for (int count = 1; ; count++) {
      Cell cell = heap.poll();
      if (count == k) return cell.value();
      int next = cell.col() + 1;
      if (next < matrix[cell.row()].length)
        heap.offer(new Cell(matrix[cell.row()][next], cell.row(), next));
    }
  }
}
