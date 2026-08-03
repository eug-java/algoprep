package com.algoprep.patterns.sortsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Counts smaller elements to the right using merge sort. */
public final class CountSmallerAfterSelf {
  private CountSmallerAfterSelf() {}
  public static List<Integer> countSmaller(int[] nums) {
    int n = nums.length; int[] counts = new int[n], indexes = new int[n]; for (int i = 0; i < n; i++) indexes[i] = i; mergeSort(nums, indexes, counts, new int[n], 0, n); return Arrays.stream(counts).boxed().toList();
  }
  private static void mergeSort(int[] nums, int[] indexes, int[] counts, int[] temp, int start, int end) { if (end - start <= 1) return; int mid = (start + end) / 2; mergeSort(nums, indexes, counts, temp, start, mid); mergeSort(nums, indexes, counts, temp, mid, end); int left = start, right = mid, out = start, moved = 0; while (left < mid || right < end) { if (right == end || (left < mid && nums[indexes[left]] <= nums[indexes[right]])) { counts[indexes[left]] += moved; temp[out++] = indexes[left++]; } else { moved++; temp[out++] = indexes[right++]; } } System.arraycopy(temp, start, indexes, start, end - start); }
}
