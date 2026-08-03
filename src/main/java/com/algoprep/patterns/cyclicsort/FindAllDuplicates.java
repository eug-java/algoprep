package com.algoprep.patterns.cyclicsort;

import java.util.ArrayList;
import java.util.List;

/** Finds values duplicated in an array containing values one through n. */
public final class FindAllDuplicates {
  private FindAllDuplicates() {}

  public static List<Integer> findNumbers(int[] nums) {
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
    List<Integer> duplicates = new ArrayList<>();
    for (int i = 0; i < nums.length; i++) if (nums[i] != i + 1) duplicates.add(nums[i]);
    return duplicates;
  }
}
