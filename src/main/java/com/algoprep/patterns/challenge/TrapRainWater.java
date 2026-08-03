package com.algoprep.patterns.challenge;

/** Computes trapped rainwater using converging boundary pointers. */
public final class TrapRainWater {
  private TrapRainWater() {}

  public static int trap(int[] height) {
    if (height == null || height.length < 3) {
      return 0;
    }

    int left = 0;
    int right = height.length - 1;
    int leftMax = 0;
    int rightMax = 0;
    int water = 0;

    while (left < right) {
      if (height[left] <= height[right]) {
        leftMax = Math.max(leftMax, height[left]);
        water += leftMax - height[left++];
      } else {
        rightMax = Math.max(rightMax, height[right]);
        water += rightMax - height[right--];
      }
    }
    return water;
  }
}
