package com.algoprep.patterns.dp;

import java.util.Arrays;

/** Length of the longest strictly increasing subsequence. O(n^2) DP. */
public final class LongestIncreasingSubsequence {
  private LongestIncreasingSubsequence() {}

  public static int lengthOfLIS(int[] nums) {
    if (nums == null || nums.length == 0) return 0;
    int[] dp = new int[nums.length];
    Arrays.fill(dp, 1);
    int best = 1;
    for (int i = 0; i < nums.length; i++) {
      for (int j = 0; j < i; j++) {
        if (nums[j] < nums[i]) dp[i] = Math.max(dp[i], dp[j] + 1);
      }
      best = Math.max(best, dp[i]);
    }
    return best;
  }
}
