package com.algoprep.patterns.challenge;

import java.util.ArrayList;
import java.util.List;

/**
 * For each nums[i], count how many later elements are strictly smaller.
 * Merge-sort tree / fenwick approaches both apply — interview may accept either pattern family.
 */
public final class CountOfSmallerNumbers {
  private CountOfSmallerNumbers() {}

  public static List<Integer> countSmaller(int[] nums) {
    if (nums == null || nums.length == 0) return List.of();
    int n = nums.length;
    int[] indexes = new int[n];
    for (int i = 0; i < n; i++) indexes[i] = i;
    int[] counts = new int[n];
    mergeSort(nums, indexes, counts, new int[n], 0, n);
    List<Integer> out = new ArrayList<>(n);
    for (int c : counts) out.add(c);
    return out;
  }

  private static void mergeSort(int[] nums, int[] indexes, int[] counts, int[] tmp, int left, int right) {
    if (right - left <= 1) return;
    int mid = (left + right) >>> 1;
    mergeSort(nums, indexes, counts, tmp, left, mid);
    mergeSort(nums, indexes, counts, tmp, mid, right);
    int i = left;
    int j = mid;
    int k = left;
    int rightTaken = 0;
    while (i < mid && j < right) {
      if (nums[indexes[j]] < nums[indexes[i]]) {
        tmp[k++] = indexes[j++];
        rightTaken++;
      } else {
        counts[indexes[i]] += rightTaken;
        tmp[k++] = indexes[i++];
      }
    }
    while (i < mid) {
      counts[indexes[i]] += rightTaken;
      tmp[k++] = indexes[i++];
    }
    while (j < right) tmp[k++] = indexes[j++];
    System.arraycopy(tmp, left, indexes, left, right - left);
  }
}
