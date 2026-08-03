package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class WordLadderTest {
  @Test
  void findsShortestTransformation() {
    assertEquals(
        5,
        WordLadder.ladderLength("hit", "cog", List.of("hot", "dot", "dog", "lot", "log", "cog")));
  }

  @Test
  void returnsZeroWhenEndWordIsUnavailableOrLengthsDiffer() {
    assertEquals(
        0, WordLadder.ladderLength("hit", "cog", List.of("hot", "dot", "dog", "lot", "log")));
    assertEquals(0, WordLadder.ladderLength("a", "long", List.of("long")));
  }
}
