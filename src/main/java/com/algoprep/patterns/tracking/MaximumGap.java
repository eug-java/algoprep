package com.algoprep.patterns.tracking;

/**
 * Maximum gap between successive values in sorted order, without sorting.
 * Each bucket stores only the min and max that landed in it.
 */
public final class MaximumGap {
  private MaximumGap() {}

  public static int maximumGap(int[] nums) {
    if (nums == null || nums.length < 2) return 0;
    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    for (int value : nums) {
      min = Math.min(min, value);
      max = Math.max(max, value);
    }
    if (min == max) return 0;
    int n = nums.length;
    long span = (long) max - min;
    int bucketSize = (int) Math.max(1L, span / (n - 1));
    int bucketCount = (int) (span / bucketSize + 1);
    int[] low = new int[bucketCount];
    int[] high = new int[bucketCount];
    boolean[] used = new boolean[bucketCount];
    for (int i = 0; i < bucketCount; i++) {
      low[i] = Integer.MAX_VALUE;
      high[i] = Integer.MIN_VALUE;
    }
    for (int value : nums) {
      int index = (int) (((long) value - min) / bucketSize);
      used[index] = true;
      low[index] = Math.min(low[index], value);
      high[index] = Math.max(high[index], value);
    }
    int gap = 0;
    int previousHigh = high[0];
    for (int i = 1; i < bucketCount; i++) {
      if (!used[i]) continue;
      gap = Math.max(gap, low[i] - previousHigh);
      previousHigh = high[i];
    }
    return gap;
  }
}
