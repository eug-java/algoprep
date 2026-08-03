package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class CountOfSmallerNumbersTest {
  @Test
  void countsSmallerToTheRight() {
    assertEquals(List.of(2, 1, 1, 0), CountOfSmallerNumbers.countSmaller(new int[] {5, 2, 6, 1}));
    assertEquals(List.of(0), CountOfSmallerNumbers.countSmaller(new int[] {-1}));
    assertEquals(List.of(0, 0), CountOfSmallerNumbers.countSmaller(new int[] {-1, -1}));
  }
}
