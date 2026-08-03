package com.algoprep.patterns.trie;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LongestCommonPrefixTest {
  @Test void findsSharedPrefix() {
    assertEquals("fl", LongestCommonPrefix.longestCommonPrefix(new String[] {"flower", "flow", "flight"}));
  }

  @Test void handlesNoSharedPrefixAndEmptyInput() {
    assertEquals("", LongestCommonPrefix.longestCommonPrefix(new String[] {"dog", "racecar", "car"}));
    assertEquals("", LongestCommonPrefix.longestCommonPrefix(new String[] {}));
  }
}
