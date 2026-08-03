package com.algoprep.patterns.stacks;

import java.util.ArrayDeque;
import java.util.Deque;

/** Calculates days until a warmer temperature for each day. */
public final class DailyTemperatures {
  private DailyTemperatures() {}

  public static int[] dailyTemperatures(int[] temperatures) {
    int[] result = new int[temperatures.length];
    Deque<Integer> unresolved = new ArrayDeque<>();
    for (int day = 0; day < temperatures.length; day++) {
      while (!unresolved.isEmpty() && temperatures[day] > temperatures[unresolved.peek()]) {
        int previous = unresolved.pop();
        result[previous] = day - previous;
      }
      unresolved.push(day);
    }
    return result;
  }
}
