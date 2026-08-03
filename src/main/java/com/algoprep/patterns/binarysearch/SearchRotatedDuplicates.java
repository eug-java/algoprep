package com.algoprep.patterns.binarysearch;

/** Searches a rotated sorted array that may contain duplicates. */
public final class SearchRotatedDuplicates {
  private SearchRotatedDuplicates() {}
  public static boolean search(int[] nums, int target) {
    if (nums == null) return false;
    int left = 0, right = nums.length - 1;
    while (left <= right) {
      int middle = left + (right - left) / 2;
      if (nums[middle] == target) return true;
      if (nums[left] == nums[middle] && nums[middle] == nums[right]) { left++; right--; }
      else if (nums[left] <= nums[middle]) {
        if (nums[left] <= target && target < nums[middle]) right = middle - 1; else left = middle + 1;
      } else {
        if (nums[middle] < target && target <= nums[right]) left = middle + 1; else right = middle - 1;
      }
    }
    return false;
  }
}
