package com.algoprep.patterns.challenge;

/** Finds the longest substring obtainable with at most {@code k} replacements. */
public final class LongestRepeatingReplacement {
  private LongestRepeatingReplacement() {}

  public static int characterReplacement(String s, int k) {
    if (s == null || s.isEmpty()) {
      return 0;
    }

    int[] frequencies = new int[Character.MAX_VALUE + 1];
    int left = 0;
    int maxFrequency = 0;
    int best = 0;

    for (int right = 0; right < s.length(); right++) {
      maxFrequency = Math.max(maxFrequency, ++frequencies[s.charAt(right)]);
      while (right - left + 1 - maxFrequency > k) {
        frequencies[s.charAt(left++)]--;
      }
      best = Math.max(best, right - left + 1);
    }
    return best;
  }
}
