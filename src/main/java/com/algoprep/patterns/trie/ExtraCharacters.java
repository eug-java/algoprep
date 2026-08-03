package com.algoprep.patterns.trie;

import java.util.HashSet;
import java.util.Set;

/** Minimizes characters not covered by dictionary words. */
public final class ExtraCharacters {
  private ExtraCharacters() {}

  public static int minExtraChar(String s, String[] dictionary) {
    Set<String> words = new HashSet<>(java.util.Arrays.asList(dictionary));
    int[] dp = new int[s.length() + 1];
    for (int i = 1; i <= s.length(); i++) {
      dp[i] = dp[i - 1] + 1;
      for (int j = 0; j < i; j++)
        if (words.contains(s.substring(j, i))) dp[i] = Math.min(dp[i], dp[j]);
    }
    return dp[s.length()];
  }
}
