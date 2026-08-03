package com.algoprep.patterns.matrices;

import java.util.ArrayDeque;

/** Finds the shortest eight-direction path through zero-valued cells. */
public final class ShortestPathBinaryMatrix {
  private ShortestPathBinaryMatrix() {}
  public static int shortestPathBinaryMatrix(int[][] grid) { int n = grid.length; if (n == 0 || grid[0].length != n || grid[0][0] != 0 || grid[n - 1][n - 1] != 0) return -1; ArrayDeque<int[]> queue = new ArrayDeque<>(); queue.add(new int[] {0, 0, 1}); grid[0][0] = 1; int[] d = {-1, 0, 1}; while (!queue.isEmpty()) { int[] point = queue.remove(); if (point[0] == n - 1 && point[1] == n - 1) return point[2]; for (int dr : d) for (int dc : d) { int r = point[0] + dr, c = point[1] + dc; if ((dr != 0 || dc != 0) && r >= 0 && r < n && c >= 0 && c < n && grid[r][c] == 0) { grid[r][c] = 1; queue.add(new int[] {r, c, point[2] + 1}); } } } return -1; }
}
