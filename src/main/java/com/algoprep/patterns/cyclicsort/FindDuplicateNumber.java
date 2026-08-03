package com.algoprep.patterns.cyclicsort;

/** Finds the repeated number using Floyd's cycle detection without mutation. */
public final class FindDuplicateNumber {
  private FindDuplicateNumber() {}
  public static int findDuplicate(int[] nums) {
    int slow = nums[0], fast = nums[0]; do { slow = nums[slow]; fast = nums[nums[fast]]; } while (slow != fast);
    slow = nums[0]; while (slow != fast) { slow = nums[slow]; fast = nums[fast]; } return slow;
  }
}
