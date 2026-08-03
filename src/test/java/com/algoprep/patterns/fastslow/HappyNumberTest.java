package com.algoprep.patterns.fastslow;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HappyNumberTest {
  @Test
  void distinguishesHappyNumbers() {
    assertTrue(HappyNumber.find(23));
    assertFalse(HappyNumber.find(12));
    assertFalse(HappyNumber.find(0));
  }
}
