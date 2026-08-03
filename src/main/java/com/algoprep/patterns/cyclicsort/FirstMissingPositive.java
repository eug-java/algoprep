package com.algoprep.patterns.cyclicsort;

/** Finds the smallest absent positive integer in linear time. */
public final class FirstMissingPositive {
  private FirstMissingPositive() {}
  public static int firstMissingPositive(int[] nums) {
    for (int i = 0; i < nums.length;) { int target = nums[i] - 1; if (target >= 0 && target < nums.length && nums[i] != nums[target]) { int temp = nums[i]; nums[i] = nums[target]; nums[target] = temp; } else i++; }
    for (int i = 0; i < nums.length; i++) if (nums[i] != i + 1) return i + 1; return nums.length + 1;
  }
}
