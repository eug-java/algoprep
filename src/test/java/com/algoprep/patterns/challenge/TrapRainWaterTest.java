package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TrapRainWaterTest {
  @Test
  void trapsWaterAcrossSeveralBasins() {
    assertEquals(6, TrapRainWater.trap(new int[] {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));
    assertEquals(9, TrapRainWater.trap(new int[] {4, 2, 0, 3, 2, 5}));
  }

  @Test
  void handlesShortOrEmptyInput() {
    assertEquals(0, TrapRainWater.trap(new int[] {1, 2}));
    assertEquals(0, TrapRainWater.trap(null));
  }
}
