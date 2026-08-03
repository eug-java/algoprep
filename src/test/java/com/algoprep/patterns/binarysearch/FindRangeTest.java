package com.algoprep.patterns.binarysearch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FindRangeTest {
  @Test
  void findsFirstAndLastOccurrences() {
    assertArrayEquals(new int[] {1, 3}, FindRange.findRange(new int[] {4, 6, 6, 6, 9}, 6));
    assertArrayEquals(new int[] {-1, -1}, FindRange.findRange(new int[] {1}, 2));
  }

  @Test
  void handlesEmpty() {
    assertArrayEquals(new int[] {-1, -1}, FindRange.findRange(new int[] {}, 1));
  }
}
