package com.algoprep.patterns.backtracking;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class SudokuSolverTest { @Test void solvesBoardInPlace() { char[][] board = {"53..7....".toCharArray(),"6..195...".toCharArray(),".98....6.".toCharArray(),"8...6...3".toCharArray(),"4..8.3..1".toCharArray(),"7...2...6".toCharArray(),".6....28.".toCharArray(),"...419..5".toCharArray(),"....8..79".toCharArray()}; SudokuSolver.solveSudoku(board); assertArrayEquals("534678912".toCharArray(), board[0]); assertArrayEquals("345286179".toCharArray(), board[8]); } }
