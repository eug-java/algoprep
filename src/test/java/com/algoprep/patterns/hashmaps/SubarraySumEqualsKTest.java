package com.algoprep.patterns.hashmaps;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SubarraySumEqualsKTest {
  @Test
  void countsOverlappingSubarrays() {
    assertEquals(2, SubarraySumEqualsK.findSubarrayCount(new int[] {1, 1, 1}, 2));
  }

  @Test
  void supportsNegativeNumbers() {
    assertEquals(3, SubarraySumEqualsK.findSubarrayCount(new int[] {1, -1, 0}, 0));
  }
}
