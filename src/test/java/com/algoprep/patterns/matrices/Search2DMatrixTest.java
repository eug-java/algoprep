package com.algoprep.patterns.matrices;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class Search2DMatrixTest { private final int[][] matrix = {{1,4,7,11},{2,5,8,12},{3,6,9,16}}; @Test void findsPresentTarget() { assertTrue(Search2DMatrix.searchMatrix(matrix, 8)); } @Test void rejectsAbsentTarget() { assertFalse(Search2DMatrix.searchMatrix(matrix, 10)); } }
