package com.algoprep.patterns.tracking;

import java.util.HashSet;
import java.util.Set;

/** Computes the longest sequence of consecutive integer values. */
public final class LongestConsecutive {
  private LongestConsecutive() {}

  public static int findLength(int[] nums) {
    Set<Integer> values = new HashSet<>();
    for (int n : nums) values.add(n);
    int longest = 0;
    for (int n : values)
      if (!values.contains(n - 1)) {
        int length = 1;
        while (values.contains(n + length)) length++;
        longest = Math.max(longest, length);
      }
    return longest;
  }
}
