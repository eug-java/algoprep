package com.algoprep.patterns.challenge;

/** Finds a sorted-array median with a logarithmic binary partition. */
public final class MedianOfTwoSortedArrays {
  private MedianOfTwoSortedArrays() {}

  public static double findMedianSortedArrays(int[] nums1, int[] nums2) {
    if (nums1 == null || nums2 == null || (nums1.length == 0 && nums2.length == 0)) {
      throw new IllegalArgumentException("At least one array must contain a value.");
    }
    if (nums1.length > nums2.length) {
      return findMedianSortedArrays(nums2, nums1);
    }

    int leftSize = (nums1.length + nums2.length + 1) / 2;
    int low = 0;
    int high = nums1.length;

    while (low <= high) {
      int partition1 = low + (high - low) / 2;
      int partition2 = leftSize - partition1;
      int left1 = partition1 == 0 ? Integer.MIN_VALUE : nums1[partition1 - 1];
      int right1 = partition1 == nums1.length ? Integer.MAX_VALUE : nums1[partition1];
      int left2 = partition2 == 0 ? Integer.MIN_VALUE : nums2[partition2 - 1];
      int right2 = partition2 == nums2.length ? Integer.MAX_VALUE : nums2[partition2];

      if (left1 <= right2 && left2 <= right1) {
        int leftMax = Math.max(left1, left2);
        if ((nums1.length + nums2.length) % 2 == 1) {
          return leftMax;
        }
        return ((double) leftMax + Math.min(right1, right2)) / 2;
      }
      if (left1 > right2) {
        high = partition1 - 1;
      } else {
        low = partition1 + 1;
      }
    }
    throw new IllegalArgumentException("Arrays must be sorted.");
  }
}
