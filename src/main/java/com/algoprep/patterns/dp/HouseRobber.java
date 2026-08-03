package com.algoprep.patterns.dp;

/** Maximizes non-adjacent house wealth that can be stolen. */
public final class HouseRobber {
  private HouseRobber() {}

  public static int findMaxSteal(int[] wealth) {
    int twoBack = 0, oneBack = 0;
    for (int value : wealth) {
      int current = Math.max(oneBack, twoBack + value);
      twoBack = oneBack;
      oneBack = current;
    }
    return oneBack;
  }
}
