package com.algoprep.patterns.slidingwindow;

import java.util.HashMap;
import java.util.Map;

/** Finds the shortest substring containing every target character. */
public final class MinWindowSubstring {
  private MinWindowSubstring() {}
  public static String minWindow(String s, String t) {
    if (s == null || t == null || s.isEmpty() || t.isEmpty()) return "";
    Map<Character, Integer> needed = new HashMap<>();
    for (char c : t.toCharArray()) needed.merge(c, 1, Integer::sum);
    int matched = 0, left = 0, bestStart = 0, bestLength = Integer.MAX_VALUE;
    for (int right = 0; right < s.length(); right++) {
      char c = s.charAt(right);
      if (needed.containsKey(c) && needed.merge(c, -1, Integer::sum) >= 0) matched++;
      while (matched == t.length()) {
        if (right - left + 1 < bestLength) { bestStart = left; bestLength = right - left + 1; }
        char removed = s.charAt(left++);
        if (needed.containsKey(removed) && needed.merge(removed, 1, Integer::sum) > 0) matched--;
      }
    }
    return bestLength == Integer.MAX_VALUE ? "" : s.substring(bestStart, bestStart + bestLength);
  }
}
