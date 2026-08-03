package com.algoprep.patterns.sortsearch;

import java.util.HashSet;
import java.util.Set;

/** Finds the unique values shared by two integer arrays. */
public final class IntersectionTwoArrays {
  private IntersectionTwoArrays() {}

  public static int[] intersection(int[] nums1, int[] nums2) {
    if (nums1 == null || nums2 == null) throw new IllegalArgumentException("arrays must not be null");
    Set<Integer> values = new HashSet<>();
    for (int value : nums1) values.add(value);
    Set<Integer> shared = new HashSet<>();
    for (int value : nums2) {
      if (values.contains(value)) shared.add(value);
    }
    return shared.stream().mapToInt(Integer::intValue).toArray();
  }
}
