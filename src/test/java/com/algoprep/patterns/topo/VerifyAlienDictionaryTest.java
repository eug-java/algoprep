package com.algoprep.patterns.topo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class VerifyAlienDictionaryTest {
  @Test void acceptsWordsSortedByCustomAlphabet() {
    assertTrue(VerifyAlienDictionary.isAlienSorted(
        new String[] {"hello", "leetcode"}, "hlabcdefgijkmnopqrstuvwxyz"));
  }

  @Test void rejectsOutOfOrderAndInvalidPrefixWords() {
    assertFalse(VerifyAlienDictionary.isAlienSorted(
        new String[] {"word", "world", "row"}, "worldabcefghijkmnpqstuvxyz"));
    assertFalse(VerifyAlienDictionary.isAlienSorted(
        new String[] {"apple", "app"}, "abcdefghijklmnopqrstuvwxyz"));
  }
}
