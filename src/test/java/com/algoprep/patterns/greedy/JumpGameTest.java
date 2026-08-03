package com.algoprep.patterns.greedy;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JumpGameTest {
  @Test
  void identifiesReachableAndBlockedArrays() {
    assertTrue(JumpGame.canJump(new int[] {2, 3, 1, 1, 4}));
    assertFalse(JumpGame.canJump(new int[] {3, 2, 1, 0, 4}));
    assertTrue(JumpGame.canJump(new int[] {0}));
  }
}
