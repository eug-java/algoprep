package com.algoprep.patterns.challenge;

/** Smallest window of s that contains t as a subsequence (not necessarily contiguous). */
public final class MinimumWindowSubsequence {
  private MinimumWindowSubsequence() {}

  public static String minWindow(String s, String t) {
    if (s == null || t == null || t.isEmpty()) return "";
    int n = s.length();
    int m = t.length();
    int bestLeft = -1;
    int bestLen = Integer.MAX_VALUE;
    for (int i = 0; i < n; i++) {
      if (s.charAt(i) != t.charAt(0)) continue;
      int si = i;
      int ti = 0;
      while (si < n && ti < m) {
        if (s.charAt(si) == t.charAt(ti)) ti++;
        si++;
      }
      if (ti < m) break;
      int end = si - 1;
      ti = m - 1;
      si = end;
      while (si >= i) {
        if (s.charAt(si) == t.charAt(ti)) {
          if (ti == 0) break;
          ti--;
        }
        si--;
      }
      int len = end - si + 1;
      if (len < bestLen) {
        bestLen = len;
        bestLeft = si;
      }
    }
    return bestLeft < 0 ? "" : s.substring(bestLeft, bestLeft + bestLen);
  }
}
