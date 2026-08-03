package com.algoprep.patterns.math;

/** Finds the largest string that divides two strings. */
public final class GcdOfStrings {
  private GcdOfStrings() {}

  public static String gcdOfStrings(String str1, String str2) {
    if (!(str1 + str2).equals(str2 + str1)) return "";
    return str1.substring(0, gcd(str1.length(), str2.length()));
  }

  private static int gcd(int a, int b) {
    while (b != 0) {
      int temp = a % b;
      a = b;
      b = temp;
    }
    return a;
  }
}
