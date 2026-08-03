package com.algoprep.patterns.slidingwindow;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LongestSubstringKDistinctTest {
  @Test
  void findsLongestWindow() {
    assertEquals(4, LongestSubstringKDistinct.findLength("araaci", 2));
    assertEquals(0, LongestSubstringKDistinct.findLength("abc", 0));
    assertEquals(0, LongestSubstringKDistinct.findLength("", 2));
  }
}
