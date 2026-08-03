package com.algoprep.patterns.binarysearch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SearchRotatedArrayTest {
  @Test
  void searchesAcrossRotation() {
    assertEquals(2, SearchRotatedArray.search(new int[] {10, 15, 1, 3, 8}, 1));
    assertEquals(-1, SearchRotatedArray.search(new int[] {4, 5, 7, 9, 10, -1, 2}, 3));
  }

  @Test
  void handlesEmpty() {
    assertEquals(-1, SearchRotatedArray.search(new int[] {}, 1));
  }
}
