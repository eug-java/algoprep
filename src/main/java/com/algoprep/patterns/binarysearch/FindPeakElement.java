package com.algoprep.patterns.binarysearch;

/** Finds an index of a peak element in logarithmic time. */
public final class FindPeakElement {
  private FindPeakElement() {}
  public static int findPeakElement(int[] nums) {
    if (nums == null || nums.length == 0) return -1;
    int left = 0, right = nums.length - 1;
    while (left < right) {
      int middle = left + (right - left) / 2;
      if (nums[middle] > nums[middle + 1]) right = middle; else left = middle + 1;
    }
    return left;
  }
}
