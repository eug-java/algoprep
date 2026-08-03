package com.algoprep.patterns.cyclicsort;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FindMissingNumberTest {
  @Test
  void findsMissingValuesAtEveryBoundary() {
    assertEquals(2, FindMissingNumber.findMissingNumber(new int[] {4, 0, 3, 1}));
    assertEquals(0, FindMissingNumber.findMissingNumber(new int[] {1, 2, 3}));
    assertEquals(3, FindMissingNumber.findMissingNumber(new int[] {0, 1, 2}));
  }
}
