package com.algoprep.patterns.challenge;

import java.util.ArrayDeque;
import java.util.Deque;

/** Computes the largest rectangle contained in a histogram. */
public final class LargestRectangleHistogram {
  private LargestRectangleHistogram() {}

  public static int largestRectangleArea(int[] heights) {
    if (heights == null || heights.length == 0) {
      return 0;
    }

    Deque<Integer> increasing = new ArrayDeque<>();
    int best = 0;
    for (int index = 0; index <= heights.length; index++) {
      int currentHeight = index == heights.length ? 0 : heights[index];
      while (!increasing.isEmpty() && heights[increasing.peek()] > currentHeight) {
        int height = heights[increasing.pop()];
        int leftBoundary = increasing.isEmpty() ? -1 : increasing.peek();
        best = Math.max(best, height * (index - leftBoundary - 1));
      }
      increasing.push(index);
    }
    return best;
  }
}
