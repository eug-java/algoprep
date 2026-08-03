package com.algoprep.patterns.sortsearch;

/** Rearranges values so adjacent comparisons alternate low then high. */
public final class WiggleSort {
  private WiggleSort() {}
  public static void wiggleSort(int[] nums) { for (int i = 1; i < nums.length; i++) if ((i % 2 == 1 && nums[i] < nums[i - 1]) || (i % 2 == 0 && nums[i] > nums[i - 1])) { int temp = nums[i]; nums[i] = nums[i - 1]; nums[i - 1] = temp; } }
}
