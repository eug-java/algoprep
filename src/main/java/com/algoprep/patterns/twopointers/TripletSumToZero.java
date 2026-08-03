package com.algoprep.patterns.twopointers;

import java.util.*;

/** Two pointers after sorting; O(n^2) time and O(1) auxiliary space. */
public final class TripletSumToZero {
  private TripletSumToZero() {}

  public static List<List<Integer>> searchTriplets(int[] arr) {
    List<List<Integer>> result = new ArrayList<>();
    if (arr == null || arr.length < 3) return result;
    Arrays.sort(arr);
    for (int i = 0; i < arr.length - 2; i++) {
      if (i > 0 && arr[i] == arr[i - 1]) continue;
      int l = i + 1, r = arr.length - 1;
      while (l < r) {
        int sum = arr[i] + arr[l] + arr[r];
        if (sum == 0) {
          result.add(List.of(arr[i], arr[l++], arr[r--]));
          while (l < r && arr[l] == arr[l - 1]) l++;
          while (l < r && arr[r] == arr[r + 1]) r--;
        } else if (sum < 0) l++;
        else r--;
      }
    }
    return result;
  }
}
