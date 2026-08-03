package com.algoprep.patterns.slidingwindow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
class LongestNoRepeatTest {
  @Test void findsLongestUniqueWindow() { assertEquals(3, LongestNoRepeat.lengthOfLongestSubstring("pwwkew")); assertEquals(0, LongestNoRepeat.lengthOfLongestSubstring("")); }
}
