package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LargestRectangleHistogramTest {
  @Test
  void findsLargestRectangleAcrossVaryingHeights() {
    assertEquals(10, LargestRectangleHistogram.largestRectangleArea(new int[] {2, 1, 5, 6, 2, 3}));
    assertEquals(4, LargestRectangleHistogram.largestRectangleArea(new int[] {2, 4}));
  }

  @Test
  void handlesEmptyAndUniformHistograms() {
    assertEquals(0, LargestRectangleHistogram.largestRectangleArea(new int[0]));
    assertEquals(6, LargestRectangleHistogram.largestRectangleArea(new int[] {2, 2, 2}));
  }
}
