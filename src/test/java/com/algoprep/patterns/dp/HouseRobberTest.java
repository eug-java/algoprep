package com.algoprep.patterns.dp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HouseRobberTest {
  @Test
  void maximizesNonAdjacentWealth() {
    assertEquals(12, HouseRobber.findMaxSteal(new int[] {2, 7, 9, 3, 1}));
    assertEquals(4, HouseRobber.findMaxSteal(new int[] {2, 1, 1, 2}));
    assertEquals(0, HouseRobber.findMaxSteal(new int[] {}));
  }
}
