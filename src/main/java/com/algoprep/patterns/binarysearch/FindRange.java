package com.algoprep.patterns.binarysearch;

/** Two binary searches for bounds; O(log n) time and O(1) space. */
public final class FindRange {
  private FindRange() {}

  public static int[] findRange(int[] arr, int key) {
    return new int[] {bound(arr, key, true), bound(arr, key, false)};
  }

  private static int bound(int[] arr, int key, boolean first) {
    if (arr == null) return -1;
    int low = 0, high = arr.length - 1, result = -1;
    while (low <= high) {
      int mid = low + (high - low) / 2;
      if (arr[mid] == key) {
        result = mid;
        if (first) high = mid - 1;
        else low = mid + 1;
      } else if (arr[mid] < key) low = mid + 1;
      else high = mid - 1;
    }
    return result;
  }
}
