package com.algoprep.patterns.bitwise;

/** Computes the common bit prefix shared by every value in an inclusive range. */
public final class BitwiseAndRange {
  private BitwiseAndRange() {}

  public static int rangeBitwiseAnd(int left, int right) {
    if (left < 0 || right < left) throw new IllegalArgumentException("range must be non-negative and ordered");
    while (left < right) right &= right - 1;
    return right;
  }
}
