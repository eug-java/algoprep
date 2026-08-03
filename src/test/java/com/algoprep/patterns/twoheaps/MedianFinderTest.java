package com.algoprep.patterns.twoheaps;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MedianFinderTest {
  @Test
  void maintainsMedianAcrossInsertions() {
    MedianFinder finder = new MedianFinder();
    finder.addNum(3);
    assertEquals(3.0, finder.findMedian());
    finder.addNum(1);
    assertEquals(2.0, finder.findMedian());
    finder.addNum(5);
    assertEquals(3.0, finder.findMedian());
  }

  @Test
  void rejectsEmptyLookup() {
    assertThrows(java.util.NoSuchElementException.class, () -> new MedianFinder().findMedian());
  }
}
