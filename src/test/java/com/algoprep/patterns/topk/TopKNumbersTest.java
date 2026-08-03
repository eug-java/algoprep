package com.algoprep.patterns.topk;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;

class TopKNumbersTest {
  @Test
  void returnsLargestInDescendingOrder() {
    assertEquals(
        List.of(12, 11, 5), TopKNumbers.findKLargestNumbers(new int[] {3, 1, 5, 12, 2, 11}, 3));
  }

  @Test
  void handlesZero() {
    assertTrue(TopKNumbers.findKLargestNumbers(new int[] {1}, 0).isEmpty());
  }
}
