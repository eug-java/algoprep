package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class LongestRepeatingReplacementTest {
  @Test
  void findsLongestReplacementWindow() {
    assertEquals(4, LongestRepeatingReplacement.characterReplacement("ABAB", 2));
    assertEquals(4, LongestRepeatingReplacement.characterReplacement("AABABBA", 1));
    assertEquals(3, LongestRepeatingReplacement.characterReplacement("BAAA", 0));
  }

  @Test
  void handlesEmptyInput() {
    assertEquals(0, LongestRepeatingReplacement.characterReplacement("", 2));
    assertEquals(0, LongestRepeatingReplacement.characterReplacement(null, 2));
  }
}
