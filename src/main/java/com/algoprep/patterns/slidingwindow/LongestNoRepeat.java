package com.algoprep.patterns.slidingwindow;

import java.util.HashMap;
import java.util.Map;

/** Finds the longest substring without repeated characters. */
public final class LongestNoRepeat {
  private LongestNoRepeat() {}
  public static int lengthOfLongestSubstring(String s) {
    if (s == null) return 0;
    Map<Character, Integer> positions = new HashMap<>();
    int left = 0, best = 0;
    for (int right = 0; right < s.length(); right++) {
      left = Math.max(left, positions.getOrDefault(s.charAt(right), -1) + 1);
      positions.put(s.charAt(right), right);
      best = Math.max(best, right - left + 1);
    }
    return best;
  }
}
