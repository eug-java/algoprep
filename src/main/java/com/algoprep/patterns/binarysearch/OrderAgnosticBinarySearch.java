package com.algoprep.patterns.binarysearch;

/** Order-agnostic binary search; O(log n) time and O(1) space. */
public final class OrderAgnosticBinarySearch {
  private OrderAgnosticBinarySearch() {}

  public static int search(int[] arr, int key) {
    if (arr == null || arr.length == 0) return -1;
    boolean ascending = arr[0] <= arr[arr.length - 1];
    int low = 0, high = arr.length - 1;
    while (low <= high) {
      int mid = low + (high - low) / 2;
      if (arr[mid] == key) return mid;
      if (ascending ? arr[mid] < key : arr[mid] > key) low = mid + 1;
      else high = mid - 1;
    }
    return -1;
  }
}
