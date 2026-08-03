package com.algoprep.patterns.twopointers;

/** Two pointers; O(n) time and O(1) space for sorted input. */
public final class RemoveDuplicates {
  private RemoveDuplicates() {}

  public static int remove(int[] arr) {
    if (arr == null || arr.length == 0) return 0;
    int next = 1;
    for (int i = 1; i < arr.length; i++) if (arr[i] != arr[next - 1]) arr[next++] = arr[i];
    return next;
  }
}
