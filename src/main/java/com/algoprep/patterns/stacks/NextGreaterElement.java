package com.algoprep.patterns.stacks;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/** Finds each first-array value's next greater value in a second array. */
public final class NextGreaterElement {
  private NextGreaterElement() {}

  public static int[] nextGreaterElement(int[] nums1, int[] nums2) {
    Map<Integer, Integer> next = new HashMap<>();
    Deque<Integer> decreasing = new ArrayDeque<>();
    for (int value : nums2) {
      while (!decreasing.isEmpty() && value > decreasing.peek()) next.put(decreasing.pop(), value);
      decreasing.push(value);
    }
    int[] result = new int[nums1.length];
    for (int i = 0; i < nums1.length; i++) result[i] = next.getOrDefault(nums1[i], -1);
    return result;
  }
}
