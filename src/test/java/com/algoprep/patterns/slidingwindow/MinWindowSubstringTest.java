package com.algoprep.patterns.slidingwindow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
class MinWindowSubstringTest {
  @Test void findsMinimumCoverOrEmptyString() { assertEquals("BANC", MinWindowSubstring.minWindow("ADOBECODEBANC", "ABC")); assertEquals("", MinWindowSubstring.minWindow("a", "aa")); }
}
