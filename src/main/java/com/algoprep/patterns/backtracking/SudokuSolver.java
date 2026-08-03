package com.algoprep.patterns.backtracking;

/** Solves a standard 9-by-9 Sudoku board in place. */
public final class SudokuSolver {
  private SudokuSolver() {}
  public static void solveSudoku(char[][] board) { if (board == null || board.length != 9) return; solve(board); }
  private static boolean solve(char[][] board) {
    for (int row = 0; row < 9; row++) for (int col = 0; col < 9; col++) if (board[row][col] == '.') {
      for (char value = '1'; value <= '9'; value++) if (valid(board, row, col, value)) { board[row][col] = value; if (solve(board)) return true; board[row][col] = '.'; }
      return false;
    }
    return true;
  }
  private static boolean valid(char[][] board, int row, int col, char value) {
    for (int i = 0; i < 9; i++) if (board[row][i] == value || board[i][col] == value || board[row / 3 * 3 + i / 3][col / 3 * 3 + i % 3] == value) return false;
    return true;
  }
}
