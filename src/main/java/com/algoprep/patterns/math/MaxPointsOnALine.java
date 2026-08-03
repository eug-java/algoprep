package com.algoprep.patterns.math;

import java.util.HashMap;
import java.util.Map;

/** Finds the greatest number of points sharing a single straight line. */
public final class MaxPointsOnALine {
  private MaxPointsOnALine() {}

  public static int maxPoints(int[][] points) {
    if (points == null) throw new IllegalArgumentException("points must not be null");
    if (points.length <= 2) return points.length;
    int maximum = 0;
    for (int origin = 0; origin < points.length; origin++) {
      validatePoint(points[origin]);
      Map<String, Integer> slopes = new HashMap<>();
      int duplicates = 1;
      int aligned = 0;
      for (int other = origin + 1; other < points.length; other++) {
        validatePoint(points[other]);
        int deltaX = points[other][0] - points[origin][0];
        int deltaY = points[other][1] - points[origin][1];
        if (deltaX == 0 && deltaY == 0) {
          duplicates++;
          continue;
        }
        int divisor = gcd(deltaX, deltaY);
        deltaX /= divisor;
        deltaY /= divisor;
        if (deltaX < 0) {
          deltaX = -deltaX;
          deltaY = -deltaY;
        } else if (deltaX == 0) {
          deltaY = 1;
        } else if (deltaY == 0) {
          deltaX = 1;
        }
        int count = slopes.merge(deltaY + "/" + deltaX, 1, Integer::sum);
        aligned = Math.max(aligned, count);
      }
      maximum = Math.max(maximum, aligned + duplicates);
    }
    return maximum;
  }

  private static void validatePoint(int[] point) {
    if (point == null || point.length != 2) throw new IllegalArgumentException("each point must have two coordinates");
  }

  private static int gcd(int first, int second) {
    first = Math.abs(first);
    second = Math.abs(second);
    while (second != 0) {
      int remainder = first % second;
      first = second;
      second = remainder;
    }
    return first;
  }
}
