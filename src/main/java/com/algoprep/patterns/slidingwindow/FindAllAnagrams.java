package com.algoprep.patterns.slidingwindow;

import java.util.*;

/** Fixed sliding window; O(s+p) time and O(p) space. */
public final class FindAllAnagrams {
  private FindAllAnagrams() {}

  public static List<Integer> findAnagrams(String s, String p) {
    List<Integer> result = new ArrayList<>();
    if (s == null || p == null || p.isEmpty() || p.length() > s.length()) return result;
    int[] need = new int[Character.MAX_VALUE + 1];
    for (char c : p.toCharArray()) need[c]++;
    int remaining = p.length();
    for (int i = 0; i < s.length(); i++) {
      if (need[s.charAt(i)]-- > 0) remaining--;
      if (i >= p.length() && ++need[s.charAt(i - p.length())] > 0) remaining++;
      if (i >= p.length() - 1 && remaining == 0) result.add(i - p.length() + 1);
    }
    return result;
  }
}
