package com.algoprep.patterns.tracking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MaximumGapTest {
  @Test
  void findsGapBetweenBuckets() {
    assertEquals(3, MaximumGap.maximumGap(new int[] {3, 6, 9, 1}));
    assertEquals(9999999, MaximumGap.maximumGap(new int[] {1, 10000000}));
  }

  @Test
  void handlesShortOrFlatInput() {
    assertEquals(0, MaximumGap.maximumGap(new int[] {7}));
    assertEquals(0, MaximumGap.maximumGap(new int[] {4, 4, 4}));
  }
}
