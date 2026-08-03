package com.algoprep.patterns.customds;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MinStackTest {
  @Test
  void tracksMinimumAcrossPops() {
    MinStack stack = new MinStack();
    stack.push(-2);
    stack.push(0);
    stack.push(-3);
    assertEquals(-3, stack.getMin());
    stack.pop();
    assertEquals(0, stack.top());
    assertEquals(-2, stack.getMin());
  }
}
