package com.algoprep.patterns.backtracking;

/** Searches a character grid for a word using adjacent cells once each. */
public final class WordSearch {
  private WordSearch() {}

  public static boolean exist(char[][] board, String word) {
    if (word.isEmpty()) return true;
    for (int row = 0; row < board.length; row++)
      for (int col = 0; col < board[row].length; col++)
        if (search(board, word, row, col, 0)) return true;
    return false;
  }

  private static boolean search(char[][] board, String word, int row, int col, int index) {
    if (index == word.length()) return true;
    if (row < 0
        || row >= board.length
        || col < 0
        || col >= board[row].length
        || board[row][col] != word.charAt(index)) return false;
    char saved = board[row][col];
    board[row][col] = '\0';
    boolean found =
        search(board, word, row + 1, col, index + 1)
            || search(board, word, row - 1, col, index + 1)
            || search(board, word, row, col + 1, index + 1)
            || search(board, word, row, col - 1, index + 1);
    board[row][col] = saved;
    return found;
  }
}
