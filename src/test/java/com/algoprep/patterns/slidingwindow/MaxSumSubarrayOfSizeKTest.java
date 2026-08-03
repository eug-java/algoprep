package com.algoprep.patterns.slidingwindow;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MaxSumSubarrayOfSizeKTest {
  @Test
  void findsMaximumWindow() {
    assertEquals(9, MaxSumSubarrayOfSizeK.findMaxSumSubArray(3, new int[] {2, 1, 5, 1, 3, 2}));
    assertEquals(-3, MaxSumSubarrayOfSizeK.findMaxSumSubArray(2, new int[] {-1, -2, -3}));
  }

  @Test
  void rejectsInvalidWindow() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MaxSumSubarrayOfSizeK.findMaxSumSubArray(0, new int[] {1}));
  }
}
