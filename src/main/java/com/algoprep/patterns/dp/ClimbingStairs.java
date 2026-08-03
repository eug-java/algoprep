package com.algoprep.patterns.dp;

/** Counts ways to climb stairs using one or two steps. */
public final class ClimbingStairs {
  private ClimbingStairs() {}

  public static int countWays(int n) {
    if (n < 0) return 0;
    int previous = 0, current = 1;
    for (int i = 1; i <= n; i++) {
      int next = previous + current;
      previous = current;
      current = next;
    }
    return current;
  }
}
