package com.algoprep.patterns.greedy;

/** Computes the minimum candies satisfying adjacent ratings constraints. */
public final class Candy {
  private Candy() {}
  public static int candy(int[] ratings) {
    if (ratings == null || ratings.length == 0) return 0;
    int[] candies = new int[ratings.length]; java.util.Arrays.fill(candies, 1);
    for (int i = 1; i < ratings.length; i++) if (ratings[i] > ratings[i - 1]) candies[i] = candies[i - 1] + 1;
    for (int i = ratings.length - 2; i >= 0; i--) if (ratings[i] > ratings[i + 1]) candies[i] = Math.max(candies[i], candies[i + 1] + 1);
    return java.util.Arrays.stream(candies).sum();
  }
}
