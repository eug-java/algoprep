package com.algoprep.patterns.bitwise;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BitwiseAndRangeTest {
  @Test void preservesOnlyCommonPrefixBits() {
    assertEquals(4, BitwiseAndRange.rangeBitwiseAnd(5, 7));
    assertEquals(0, BitwiseAndRange.rangeBitwiseAnd(0, 1));
  }

  @Test void handlesSingleValueAndLargeRange() {
    assertEquals(12, BitwiseAndRange.rangeBitwiseAnd(12, 12));
    assertEquals(0, BitwiseAndRange.rangeBitwiseAnd(1, Integer.MAX_VALUE));
  }
}
