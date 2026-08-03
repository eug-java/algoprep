package com.algoprep.patterns.math;

/** Raises a number to an integer power by binary exponentiation. */
public final class PowXN {
  private PowXN() {}

  public static double myPow(double x, int n) {
    long exponent = n;
    if (exponent < 0) {
      x = 1 / x;
      exponent = -exponent;
    }
    double result = 1;
    while (exponent > 0) {
      if ((exponent & 1) == 1) result *= x;
      x *= x;
      exponent >>= 1;
    }
    return result;
  }
}
