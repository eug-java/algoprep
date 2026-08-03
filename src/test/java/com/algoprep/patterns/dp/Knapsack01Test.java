package com.algoprep.patterns.dp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class Knapsack01Test {
  @Test
  void choosesMostProfitableFittingItems() {
    assertEquals(22, Knapsack01.solveKnapsack(new int[] {1, 6, 10, 16}, new int[] {1, 2, 3, 5}, 7));
    assertEquals(0, Knapsack01.solveKnapsack(new int[] {5}, new int[] {2}, 0));
  }

  @Test
  void rejectsMismatchedInputs() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Knapsack01.solveKnapsack(new int[] {1}, new int[] {1, 2}, 3));
  }
}
