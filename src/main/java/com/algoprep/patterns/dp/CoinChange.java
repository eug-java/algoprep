package com.algoprep.patterns.dp;

import java.util.Arrays;

/** Finds the fewest coins needed to reach an amount. */
public final class CoinChange {
  private CoinChange() {}
  public static int coinChange(int[] coins, int amount) {
    if (amount < 0) return -1; int[] dp = new int[amount + 1]; Arrays.fill(dp, amount + 1); dp[0] = 0;
    for (int value = 1; value <= amount; value++) for (int coin : coins) if (coin > 0 && coin <= value) dp[value] = Math.min(dp[value], dp[value - coin] + 1);
    return dp[amount] > amount ? -1 : dp[amount];
  }
}
