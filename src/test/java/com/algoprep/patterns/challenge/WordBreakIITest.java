package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class WordBreakIITest {
  @Test
  void findsAllSentences() {
    List<String> result = WordBreakII.wordBreak("catsanddog", List.of("cat", "cats", "and", "sand", "dog"));
    Set<String> set = new HashSet<>(result);
    assertEquals(2, set.size());
    assertTrue(set.contains("cats and dog"));
    assertTrue(set.contains("cat sand dog"));
  }
}
