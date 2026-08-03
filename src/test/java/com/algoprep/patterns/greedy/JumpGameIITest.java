package com.algoprep.patterns.greedy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JumpGameIITest {
  @Test
  void findsMinimumJumpsAndSignalsUnreachableInput() {
    assertEquals(2, JumpGameII.countMinJumps(new int[] {2, 3, 1, 1, 4}));
    assertEquals(0, JumpGameII.countMinJumps(new int[] {0}));
    assertEquals(-1, JumpGameII.countMinJumps(new int[] {1, 0, 1}));
  }
}
