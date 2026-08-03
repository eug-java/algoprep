package com.algoprep.patterns.cyclicsort;

/** Sorts an array containing exactly the values one through n. */
public final class CyclicSort {
  private CyclicSort() {}

  public static void sort(int[] nums) {
    int index = 0;
    while (index < nums.length) {
      int correct = nums[index] - 1;
      if (nums[index] != nums[correct]) {
        int temp = nums[index];
        nums[index] = nums[correct];
        nums[correct] = temp;
      } else {
        index++;
      }
    }
  }
}
