package com.algoprep.patterns.fastslow;

/** Detects a directional cycle of length greater than one. */
public final class CircularArrayLoop {
  private CircularArrayLoop() {}

  public static boolean circularArrayLoop(int[] nums) {
    if (nums == null || nums.length < 2) return false;
    for (int start = 0; start < nums.length; start++) {
      boolean forward = nums[start] > 0;
      int slow = start, fast = start;
      do {
        slow = next(nums, forward, slow);
        fast = next(nums, forward, fast);
        if (fast != -1) fast = next(nums, forward, fast);
      } while (slow != -1 && fast != -1 && slow != fast);
      if (slow != -1 && slow == fast) return true;
    }
    return false;
  }

  private static int next(int[] nums, boolean forward, int index) {
    if ((nums[index] > 0) != forward) return -1;
    int next = Math.floorMod(index + nums[index], nums.length);
    return next == index ? -1 : next;
  }
}
