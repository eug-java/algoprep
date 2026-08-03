package com.algoprep.patterns.twopointers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Returns unique quadruplets whose sum equals a target. */
public final class QuadrupleSum {
  private QuadrupleSum() {}

  public static List<List<Integer>> searchQuadruplets(int[] arr, int target) {
    List<List<Integer>> result = new ArrayList<>();
    if (arr == null || arr.length < 4) return result;
    Arrays.sort(arr);
    for (int first = 0; first < arr.length - 3; first++) {
      if (first > 0 && arr[first] == arr[first - 1]) continue;
      for (int second = first + 1; second < arr.length - 2; second++) {
        if (second > first + 1 && arr[second] == arr[second - 1]) continue;
        int left = second + 1, right = arr.length - 1;
        while (left < right) {
          long sum = (long) arr[first] + arr[second] + arr[left] + arr[right];
          if (sum == target) {
            result.add(List.of(arr[first], arr[second], arr[left++], arr[right--]));
            while (left < right && arr[left] == arr[left - 1]) left++;
            while (left < right && arr[right] == arr[right + 1]) right--;
          } else if (sum < target) left++;
          else right--;
        }
      }
    }
    return result;
  }
}
