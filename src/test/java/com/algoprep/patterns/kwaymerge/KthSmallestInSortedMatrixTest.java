package com.algoprep.patterns.kwaymerge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class KthSmallestInSortedMatrixTest {

  @Test
  void findsKthAcrossRows() {
    // Sorted values: 1,2,3,3,4,6,6,7,8 → 5th is 4
    assertEquals(
        4,
        KthSmallestInSortedMatrix.findKthSmallest(
            new int[][] {{1, 3, 4}, {2, 6, 8}, {3, 6, 7}}, 5));
  }

  @Test
  void classicMatrixExample() {
    assertEquals(
        13,
        KthSmallestInSortedMatrix.findKthSmallest(
            new int[][] {{1, 5, 9}, {10, 11, 13}, {12, 13, 15}}, 8));
  }

  @Test
  void rejectsOutOfRangeK() {
    assertThrows(
        IllegalArgumentException.class,
        () -> KthSmallestInSortedMatrix.findKthSmallest(new int[][] {{1}}, 2));
  }
}
