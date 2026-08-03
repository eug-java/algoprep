package com.algoprep.patterns.bitwise;

/** Counts set bits in a 32-bit integer as an unsigned value. */
public final class NumberOf1Bits {
  private NumberOf1Bits() {}

  public static int countSetBits(int n) {
    int count = 0;
    while (n != 0) {
      count += n & 1;
      n >>>= 1;
    }
    return count;
  }
}
