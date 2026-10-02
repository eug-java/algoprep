package com.algoprep.patterns.dp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LongestIncreasingSubsequenceTest {
  @Test
  void findsLength() {
    assertEquals(4, LongestIncreasingSubsequence.lengthOfLIS(new int[] {10, 9, 2, 5, 3, 7, 101, 18}));
    assertEquals(1, LongestIncreasingSubsequence.lengthOfLIS(new int[] {7, 7, 7}));
  }

  @Test
  void handlesEmpty() {
    assertEquals(0, LongestIncreasingSubsequence.lengthOfLIS(new int[0]));
  }
}
