package com.algoprep.patterns.backtracking;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class CombinationSumTest {
  @Test
  void findsAllUniqueCombinations() {
    assertEquals(
        List.of(List.of(2, 2, 3), List.of(7)),
        CombinationSum.findCombinations(new int[] {2, 3, 6, 7}, 7));
    assertEquals(List.of(), CombinationSum.findCombinations(new int[] {2}, 1));
  }
}
