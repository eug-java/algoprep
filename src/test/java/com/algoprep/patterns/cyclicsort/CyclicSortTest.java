package com.algoprep.patterns.cyclicsort;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class CyclicSortTest {
  @Test
  void sortsOneThroughNInPlace() {
    int[] numbers = {3, 1, 5, 4, 2};
    CyclicSort.sort(numbers);
    assertArrayEquals(new int[] {1, 2, 3, 4, 5}, numbers);
  }
}
