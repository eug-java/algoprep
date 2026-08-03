package com.algoprep.patterns.backtracking;

import java.util.ArrayList;
import java.util.List;

/** Places n queens on an n-by-n board without conflicts. */
public final class NQueens {
  private NQueens() {}
  public static List<List<String>> solveNQueens(int n) {
    List<List<String>> result = new ArrayList<>(); if (n < 1) return result;
    search(0, n, new boolean[n], new boolean[2 * n], new boolean[2 * n], new ArrayList<>(), result); return result;
  }
  private static void search(int row, int n, boolean[] columns, boolean[] down, boolean[] up, List<String> board, List<List<String>> result) {
    if (row == n) { result.add(new ArrayList<>(board)); return; }
    for (int col = 0; col < n; col++) { int d = row - col + n, u = row + col; if (columns[col] || down[d] || up[u]) continue;
      columns[col] = down[d] = up[u] = true; board.add(".".repeat(col) + "Q" + ".".repeat(n - col - 1)); search(row + 1, n, columns, down, up, board, result); board.remove(board.size() - 1); columns[col] = down[d] = up[u] = false;
    }
  }
}
