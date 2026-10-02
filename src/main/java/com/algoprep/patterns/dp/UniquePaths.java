package com.algoprep.patterns.dp;

import java.util.Arrays;

/** Number of ways to walk from the top-left cell to the bottom-right, moving only right or down. */
public final class UniquePaths {
  private UniquePaths() {}

  public static int uniquePaths(int m, int n) {
    if (m <= 0 || n <= 0) return 0;
    int[] dp = new int[n];
    Arrays.fill(dp, 1);
    for (int row = 1; row < m; row++) {
      for (int col = 1; col < n; col++) dp[col] += dp[col - 1];
    }
    return dp[n - 1];
  }
}
