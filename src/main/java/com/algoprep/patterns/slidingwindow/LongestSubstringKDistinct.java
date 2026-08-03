package com.algoprep.patterns.slidingwindow;

import java.util.*;

/** Variable sliding window; O(n) time and O(k) space. */
public final class LongestSubstringKDistinct {
  private LongestSubstringKDistinct() {}

  public static int findLength(String str, int k) {
    if (str == null || k <= 0) return 0;
    Map<Character, Integer> counts = new HashMap<>();
    int start = 0, max = 0;
    for (int end = 0; end < str.length(); end++) {
      counts.merge(str.charAt(end), 1, Integer::sum);
      while (counts.size() > k) {
        char c = str.charAt(start++);
        if (counts.merge(c, -1, Integer::sum) == 0) counts.remove(c);
      }
      max = Math.max(max, end - start + 1);
    }
    return max;
  }
}
