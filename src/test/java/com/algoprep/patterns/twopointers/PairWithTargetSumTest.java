package com.algoprep.patterns.twopointers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PairWithTargetSumTest {
  @Test
  void findsPairAndHandlesMissing() {
    assertArrayEquals(new int[] {1, 3}, PairWithTargetSum.search(new int[] {1, 2, 3, 4}, 6));
    assertArrayEquals(new int[] {-1, -1}, PairWithTargetSum.search(new int[] {1}, 1));
    assertArrayEquals(new int[] {-1, -1}, PairWithTargetSum.search(new int[] {}, 1));
  }
}
