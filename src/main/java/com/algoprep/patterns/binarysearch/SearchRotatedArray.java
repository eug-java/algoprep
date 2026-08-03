package com.algoprep.patterns.binarysearch;

/** Binary search in a rotated distinct sorted array; O(log n) time and O(1) space. */
public final class SearchRotatedArray {
  private SearchRotatedArray() {}

  public static int search(int[] arr, int key) {
    if (arr == null) return -1;
    int low = 0, high = arr.length - 1;
    while (low <= high) {
      int mid = low + (high - low) / 2;
      if (arr[mid] == key) return mid;
      if (arr[low] <= arr[mid]) {
        if (key >= arr[low] && key < arr[mid]) high = mid - 1;
        else low = mid + 1;
      } else {
        if (key > arr[mid] && key <= arr[high]) low = mid + 1;
        else high = mid - 1;
      }
    }
    return -1;
  }
}
