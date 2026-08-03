package com.algoprep.patterns.tracking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MajorityElementTest {
  @Test
  void findsMajorityVoteCandidate() {
    assertEquals(2, MajorityElement.findMajority(new int[] {2, 2, 1, 1, 1, 2, 2}));
  }
}
