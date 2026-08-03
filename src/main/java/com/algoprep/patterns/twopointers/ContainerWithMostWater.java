package com.algoprep.patterns.twopointers;

/** Finds the largest area bounded by two vertical lines. */
public final class ContainerWithMostWater {
  private ContainerWithMostWater() {}

  public static int maxArea(int[] height) {
    if (height == null || height.length < 2) return 0;
    int left = 0, right = height.length - 1, best = 0;
    while (left < right) {
      best = Math.max(best, (right - left) * Math.min(height[left], height[right]));
      if (height[left] < height[right]) left++;
      else right--;
    }
    return best;
  }
}
