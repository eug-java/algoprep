package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MinimumWindowSubsequenceTest {
  @Test
  void findsMinimalSubsequenceWindow() {
    assertEquals("bcde", MinimumWindowSubsequence.minWindow("abcdebdde", "bde"));
    assertEquals("bde", MinimumWindowSubsequence.minWindow("bde", "bde"));
    assertEquals("", MinimumWindowSubsequence.minWindow("abc", "acx"));
  }
}
