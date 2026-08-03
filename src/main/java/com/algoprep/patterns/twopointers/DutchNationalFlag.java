package com.algoprep.patterns.twopointers;

/** Sorts values 0, 1, and 2 in one pass. */
public final class DutchNationalFlag {
  private DutchNationalFlag() {}

  public static void sortColors(int[] nums) {
    if (nums == null) return;
    int low = 0, current = 0, high = nums.length - 1;
    while (current <= high) {
      if (nums[current] == 0) swap(nums, low++, current++);
      else if (nums[current] == 2) swap(nums, current, high--);
      else current++;
    }
  }

  private static void swap(int[] values, int a, int b) {
    int value = values[a]; values[a] = values[b]; values[b] = value;
  }
}
