package com.algoprep.patterns.bitwise;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NumberOf1BitsTest {
  @Test
  void countsPositiveBits() {
    assertEquals(3, NumberOf1Bits.countSetBits(0b1011));
  }

  @Test
  void treatsNegativeAsUnsigned32Bit() {
    assertEquals(32, NumberOf1Bits.countSetBits(-1));
  }
}
