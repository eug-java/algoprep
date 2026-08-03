package com.algoprep.patterns.trie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ExtraCharactersTest {
  @Test
  void minimizesUnmatchedCharacters() {
    assertEquals(
        1, ExtraCharacters.minExtraChar("leetscode", new String[] {"leet", "code", "leetcode"}));
  }

  @Test
  void matchesEntireString() {
    assertEquals(0, ExtraCharacters.minExtraChar("applepenapple", new String[] {"apple", "pen"}));
  }
}
