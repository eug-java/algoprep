package com.algoprep.patterns.cyclicsort;

/** Finds the missing value from an array containing zero through n. */
public final class FindMissingNumber {
  private FindMissingNumber() {}

  public static int findMissingNumber(int[] nums) {
    int index = 0;
    while (index < nums.length) {
      int value = nums[index];
      if (value < nums.length && value != nums[value]) {
        int temp = nums[index];
        nums[index] = nums[value];
        nums[value] = temp;
      } else {
        index++;
      }
    }
    for (int i = 0; i < nums.length; i++) if (nums[i] != i) return i;
    return nums.length;
  }
}
