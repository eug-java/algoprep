package com.algoprep.patterns.graphs;

/** Counts connected land regions in a grid. */
public final class NumberOfIslands {
  private NumberOfIslands() {}

  public static int countIslands(char[][] grid) {
    if (grid == null || grid.length == 0) return 0;
    int count = 0;
    for (int r = 0; r < grid.length; r++)
      for (int c = 0; c < grid[r].length; c++)
        if (grid[r][c] == '1') {
          flood(grid, r, c);
          count++;
        }
    return count;
  }

  private static void flood(char[][] g, int r, int c) {
    if (r < 0 || r >= g.length || c < 0 || c >= g[r].length || g[r][c] != '1') return;
    g[r][c] = '0';
    flood(g, r + 1, c);
    flood(g, r - 1, c);
    flood(g, r, c + 1);
    flood(g, r, c - 1);
  }
}
