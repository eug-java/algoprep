package com.algoprep.patterns.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class GcdOfStringsTest {
  @Test
  void findsLargestCommonDivisor() {
    assertEquals("ABC", GcdOfStrings.gcdOfStrings("ABCABC", "ABC"));
  }

  @Test
  void rejectsNonDivisibleStrings() {
    assertEquals("", GcdOfStrings.gcdOfStrings("LEET", "CODE"));
  }
}
