package com.algoprep.patterns.twoheaps;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class StaticMedianTest {
  @Test void findsOddAndEvenMedians() {
    assertEquals(2.0, StaticMedian.findMedian(new int[] {3, 1, 2}));
    assertEquals(2.5, StaticMedian.findMedian(new int[] {4, 1, 3, 2}));
  }

  @Test void handlesNegativeValuesAndRejectsEmptyInput() {
    assertEquals(-2.0, StaticMedian.findMedian(new int[] {-1, -3, -2}));
    assertThrows(IllegalArgumentException.class, () -> StaticMedian.findMedian(new int[] {}));
  }
}
