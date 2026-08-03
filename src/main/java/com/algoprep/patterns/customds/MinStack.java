package com.algoprep.patterns.customds;

import java.util.ArrayDeque;
import java.util.Deque;

/** Stack that retrieves its current minimum in constant time. */
public class MinStack {
  private final Deque<Integer> values = new ArrayDeque<>();
  private final Deque<Integer> minimums = new ArrayDeque<>();

  public void push(int value) {
    values.push(value);
    if (minimums.isEmpty() || value <= minimums.peek()) minimums.push(value);
  }

  public void pop() {
    int value = values.pop();
    if (value == minimums.peek()) minimums.pop();
  }

  public int top() {
    return values.peek();
  }

  public int getMin() {
    return minimums.peek();
  }
}
