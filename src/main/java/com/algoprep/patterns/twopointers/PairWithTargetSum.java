package com.algoprep.patterns.twopointers;

/** Two pointers; O(n) time and O(1) space for sorted input. */
public final class PairWithTargetSum {
  private PairWithTargetSum() {}

  public static int[] search(int[] arr, int targetSum) {
    if (arr == null) return new int[] {-1, -1};
    int left = 0, right = arr.length - 1;
    while (left < right) {
      int sum = arr[left] + arr[right];
      if (sum == targetSum) return new int[] {left, right};
      if (sum < targetSum) left++;
      else right--;
    }
    return new int[] {-1, -1};
  }
}
