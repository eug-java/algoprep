package com.algoprep.patterns.tracking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LongestConsecutiveTest {
  @Test
  void findsUnsortedRun() {
    assertEquals(4, LongestConsecutive.findLength(new int[] {100, 4, 200, 1, 3, 2}));
  }

  @Test
  void handlesDuplicates() {
    assertEquals(3, LongestConsecutive.findLength(new int[] {1, 2, 0, 1}));
  }
}
