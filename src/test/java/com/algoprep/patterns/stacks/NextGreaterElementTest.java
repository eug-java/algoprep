package com.algoprep.patterns.stacks;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class NextGreaterElementTest {
  @Test
  void findsNextGreaterValuesOrMissingMarker() {
    assertArrayEquals(
        new int[] {-1, 3, -1},
        NextGreaterElement.nextGreaterElement(new int[] {4, 1, 2}, new int[] {1, 3, 4, 2}));
    assertArrayEquals(
        new int[] {3, -1},
        NextGreaterElement.nextGreaterElement(new int[] {2, 4}, new int[] {1, 2, 3, 4}));
  }
}
