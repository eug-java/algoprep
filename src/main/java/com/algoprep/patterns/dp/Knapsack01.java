package com.algoprep.patterns.dp;

/** Solves the zero-one knapsack maximization problem. */
public final class Knapsack01 {
  private Knapsack01() {}

  public static int solveKnapsack(int[] profits, int[] weights, int capacity) {
    if (profits.length != weights.length) {
      throw new IllegalArgumentException("profits and weights must have equal lengths");
    }
    int[] best = new int[capacity + 1];
    for (int i = 0; i < profits.length; i++) {
      for (int c = capacity; c >= weights[i]; c--) {
        best[c] = Math.max(best[c], profits[i] + best[c - weights[i]]);
      }
    }
    return best[capacity];
  }
}
