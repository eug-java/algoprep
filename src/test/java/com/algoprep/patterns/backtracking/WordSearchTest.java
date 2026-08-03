package com.algoprep.patterns.backtracking;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WordSearchTest {
  @Test
  void findsWordsWithoutReusingCells() {
    char[][] board = {{'A', 'B', 'C', 'E'}, {'S', 'F', 'C', 'S'}, {'A', 'D', 'E', 'E'}};
    assertTrue(WordSearch.exist(board, "ABCCED"));
    assertTrue(WordSearch.exist(board, "SEE"));
    assertFalse(WordSearch.exist(board, "ABCB"));
  }

  @Test
  void emptyWordExists() {
    assertTrue(WordSearch.exist(new char[][] {{'A'}}, ""));
  }
}
