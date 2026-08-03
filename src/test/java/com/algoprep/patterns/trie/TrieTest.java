package com.algoprep.patterns.trie;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TrieTest {
  @Test
  void distinguishesWordsFromPrefixes() {
    Trie trie = new Trie();
    trie.insert("apple");
    assertAll(
        () -> assertTrue(trie.search("apple")),
        () -> assertFalse(trie.search("app")),
        () -> assertTrue(trie.startsWith("app")));
  }
}
