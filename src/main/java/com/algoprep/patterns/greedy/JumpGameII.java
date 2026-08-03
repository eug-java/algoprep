package com.algoprep.patterns.greedy;

/** Finds the minimum jumps needed to reach the final index. */
public final class JumpGameII {
  private JumpGameII() {}

  public static int countMinJumps(int[] jumps) {
    if (jumps.length < 2) return 0;
    int jumpsTaken = 0, currentEnd = 0, furthest = 0;
    for (int i = 0; i < jumps.length - 1; i++) {
      furthest = Math.max(furthest, i + jumps[i]);
      if (i == currentEnd) {
        if (furthest == currentEnd) return -1;
        jumpsTaken++;
        currentEnd = furthest;
      }
    }
    return jumpsTaken;
  }
}
