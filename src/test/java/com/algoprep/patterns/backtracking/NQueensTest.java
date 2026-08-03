package com.algoprep.patterns.backtracking;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class NQueensTest { @Test void findsTwoFourQueenSolutions() { var solutions = NQueens.solveNQueens(4); assertEquals(2, solutions.size()); assertTrue(solutions.stream().allMatch(board -> board.size() == 4)); } @Test void handlesSingleQueen() { assertEquals(java.util.List.of(java.util.List.of("Q")), NQueens.solveNQueens(1)); } }
