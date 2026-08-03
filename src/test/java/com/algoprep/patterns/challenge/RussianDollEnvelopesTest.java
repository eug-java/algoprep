package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RussianDollEnvelopesTest {
  @Test
  void findsLongestChain() {
    assertEquals(3, RussianDollEnvelopes.maxEnvelopes(new int[][] {{5, 4}, {6, 4}, {6, 7}, {2, 3}}));
    assertEquals(1, RussianDollEnvelopes.maxEnvelopes(new int[][] {{1, 1}, {1, 1}}));
    assertEquals(0, RussianDollEnvelopes.maxEnvelopes(new int[][] {}));
  }
}
