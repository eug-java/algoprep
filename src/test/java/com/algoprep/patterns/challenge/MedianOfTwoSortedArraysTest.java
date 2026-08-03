package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MedianOfTwoSortedArraysTest {
  @Test
  void findsOddAndEvenMedians() {
    assertEquals(
        2.0, MedianOfTwoSortedArrays.findMedianSortedArrays(new int[] {1, 3}, new int[] {2}));
    assertEquals(
        2.5, MedianOfTwoSortedArrays.findMedianSortedArrays(new int[] {1, 2}, new int[] {3, 4}));
  }

  @Test
  void handlesAnEmptySmallerArrayAndExtremeValues() {
    assertEquals(
        4.0, MedianOfTwoSortedArrays.findMedianSortedArrays(new int[0], new int[] {3, 4, 5}));
    assertEquals(
        -0.5,
        MedianOfTwoSortedArrays.findMedianSortedArrays(
            new int[] {Integer.MIN_VALUE}, new int[] {Integer.MAX_VALUE}));
  }

  @Test
  void rejectsTwoEmptyArrays() {
    assertThrows(
        IllegalArgumentException.class,
        () -> MedianOfTwoSortedArrays.findMedianSortedArrays(new int[0], new int[0]));
  }
}
