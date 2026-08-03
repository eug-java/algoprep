package com.algoprep.patterns.slidingwindow;

/** Fixed sliding window; O(n) time and O(1) space. */
public final class MaxSumSubarrayOfSizeK {
  private MaxSumSubarrayOfSizeK() {}

  public static int findMaxSumSubArray(int k, int[] arr) {
    if (arr == null || k <= 0 || k > arr.length)
      throw new IllegalArgumentException("k must be within array bounds");
    int sum = 0, max = Integer.MIN_VALUE;
    for (int end = 0; end < arr.length; end++) {
      sum += arr[end];
      if (end >= k) sum -= arr[end - k];
      if (end >= k - 1) max = Math.max(max, sum);
    }
    return max;
  }
}
