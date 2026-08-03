package com.algoprep.patterns.challenge;

import java.util.Arrays;

/** Longest increasing envelope chain — sort width asc / height desc, then LIS on heights. */
public final class RussianDollEnvelopes {
  private RussianDollEnvelopes() {}

  public static int maxEnvelopes(int[][] envelopes) {
    if (envelopes == null || envelopes.length == 0) return 0;
    Arrays.sort(envelopes, (a, b) -> a[0] == b[0] ? Integer.compare(b[1], a[1]) : Integer.compare(a[0], b[0]));
    int[] tails = new int[envelopes.length];
    int size = 0;
    for (int[] env : envelopes) {
      int h = env[1];
      int lo = 0, hi = size;
      while (lo < hi) {
        int mid = (lo + hi) >>> 1;
        if (tails[mid] < h) lo = mid + 1;
        else hi = mid;
      }
      tails[lo] = h;
      if (lo == size) size++;
    }
    return size;
  }
}
