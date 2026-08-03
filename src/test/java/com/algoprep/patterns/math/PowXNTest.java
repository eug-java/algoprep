package com.algoprep.patterns.math;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PowXNTest {
  @Test
  void supportsPositiveAndNegativeExponents() {
    assertEquals(1024d, PowXN.myPow(2, 10));
    assertEquals(.25d, PowXN.myPow(2, -2));
  }

  @Test
  void supportsMinimumIntegerExponent() {
    assertEquals(0d, PowXN.myPow(2, Integer.MIN_VALUE));
  }
}
