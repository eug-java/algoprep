package com.algoprep.patterns.matrices;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class ShortestPathBinaryMatrixTest { @Test void findsDiagonalPath() { assertEquals(2, ShortestPathBinaryMatrix.shortestPathBinaryMatrix(new int[][] {{0,1},{1,0}})); } @Test void reportsBlockedPath() { assertEquals(-1, ShortestPathBinaryMatrix.shortestPathBinaryMatrix(new int[][] {{0,1},{1,1}})); } }
