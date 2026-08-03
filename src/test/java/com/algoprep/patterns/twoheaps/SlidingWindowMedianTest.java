package com.algoprep.patterns.twoheaps;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;
class SlidingWindowMedianTest {
  @Test void calculatesOddAndEvenWindowMedians() { assertArrayEquals(new double[] {1,-1,-1,3,5,6}, SlidingWindowMedian.medianSlidingWindow(new int[] {1,3,-1,-3,5,3,6,7},3)); assertArrayEquals(new double[] {1.5,2.5}, SlidingWindowMedian.medianSlidingWindow(new int[] {1,2,3},2)); }
}
