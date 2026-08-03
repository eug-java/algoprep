package com.algoprep.patterns.hashmaps;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class TwoSumTest {
  @Test
  void returnsTargetPairIndices() {
    assertArrayEquals(new int[] {0, 1}, TwoSum.findPair(new int[] {2, 7, 11, 15}, 9));
  }

  @Test
  void returnsEmptyWhenAbsent() {
    assertArrayEquals(new int[0], TwoSum.findPair(new int[] {1, 2}, 9));
  }
}
