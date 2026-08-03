package com.algoprep.patterns.fastslow;

/** Fast and slow pointers on digit-square sums; O(log n) space-free iteration. */
public final class HappyNumber {
  private HappyNumber() {}

  public static boolean find(int num) {
    if (num <= 0) return false;
    int slow = num, fast = num;
    do {
      slow = squareSum(slow);
      fast = squareSum(squareSum(fast));
    } while (slow != fast);
    return slow == 1;
  }

  private static int squareSum(int n) {
    int sum = 0;
    while (n > 0) {
      int digit = n % 10;
      sum += digit * digit;
      n /= 10;
    }
    return sum;
  }
}
