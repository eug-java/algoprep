package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class SlidingWindowMaximumTest {
  @Test
  void findsWindowMaxima() {
    assertArrayEquals(new int[] {3, 3, 5, 5, 6, 7},
        SlidingWindowMaximum.maxSlidingWindow(new int[] {1, 3, -1, -3, 5, 3, 6, 7}, 3));
    assertArrayEquals(new int[] {1}, SlidingWindowMaximum.maxSlidingWindow(new int[] {1}, 1));
    assertArrayEquals(new int[] {7, 4, 7}, SlidingWindowMaximum.maxSlidingWindow(new int[] {7, 2, 4, 7}, 2));
  }
}
