package com.algoprep.patterns.bitwise;

/** Finds the non-duplicated number using XOR. */
public final class SingleNumber {
  private SingleNumber() {}

  public static int findSingleNumber(int[] arr) {
    int unique = 0;
    for (int n : arr) unique ^= n;
    return unique;
  }
}
