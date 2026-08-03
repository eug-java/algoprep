package com.algoprep.patterns.bitwise;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SingleNumberTest {
  @Test
  void findsValueWithoutDuplicate() {
    assertEquals(4, SingleNumber.findSingleNumber(new int[] {4, 1, 2, 1, 2}));
  }
}
