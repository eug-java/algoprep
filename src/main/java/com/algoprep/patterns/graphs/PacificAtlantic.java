package com.algoprep.patterns.graphs;

import java.util.ArrayList;
import java.util.List;

/** Finds cells from which water can reach both oceans. */
public final class PacificAtlantic {
  private PacificAtlantic() {}
  public static List<List<Integer>> pacificAtlantic(int[][] heights) { List<List<Integer>> result = new ArrayList<>(); if (heights == null || heights.length == 0 || heights[0].length == 0) return result; int rows = heights.length, cols = heights[0].length; boolean[][] pacific = new boolean[rows][cols], atlantic = new boolean[rows][cols]; for (int r = 0; r < rows; r++) { visit(heights, pacific, r, 0); visit(heights, atlantic, r, cols - 1); } for (int c = 0; c < cols; c++) { visit(heights, pacific, 0, c); visit(heights, atlantic, rows - 1, c); } for (int r = 0; r < rows; r++) for (int c = 0; c < cols; c++) if (pacific[r][c] && atlantic[r][c]) result.add(List.of(r, c)); return result; }
  private static void visit(int[][] h, boolean[][] seen, int row, int col) { if (seen[row][col]) return; seen[row][col] = true; int[] d = {-1, 0, 1, 0, -1}; for (int i = 0; i < 4; i++) { int r = row + d[i], c = col + d[i + 1]; if (r >= 0 && r < h.length && c >= 0 && c < h[0].length && !seen[r][c] && h[r][c] >= h[row][col]) visit(h, seen, r, c); } }
}
