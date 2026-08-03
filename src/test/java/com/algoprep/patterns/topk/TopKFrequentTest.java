package com.algoprep.patterns.topk;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;

class TopKFrequentTest {
  @Test
  void returnsMostFrequentFirst() {
    assertEquals(
        List.of(12, 11),
        TopKFrequent.findTopKFrequentNumbers(new int[] {1, 3, 5, 12, 11, 12, 11}, 2));
  }

  @Test
  void handlesZero() {
    assertTrue(TopKFrequent.findTopKFrequentNumbers(new int[] {1}, 0).isEmpty());
  }
}
