package com.algoprep.patterns.greedy;

import java.util.Arrays;

/** Assigns the smallest sufficient cookie to maximize content children. */
public final class AssignCookies {
  private AssignCookies() {}

  public static int findContentChildren(int[] g, int[] s) {
    if (g == null || s == null) throw new IllegalArgumentException("arrays must not be null");
    int[] greed = g.clone();
    int[] cookies = s.clone();
    Arrays.sort(greed);
    Arrays.sort(cookies);
    int child = 0;
    for (int cookie : cookies) {
      if (child < greed.length && cookie >= greed[child]) child++;
    }
    return child;
  }
}
