package com.algoprep.patterns.stacks;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

class DailyTemperaturesTest {
  @Test
  void findsWaitsForWarmerDays() {
    assertArrayEquals(
        new int[] {1, 1, 4, 2, 1, 1, 0, 0},
        DailyTemperatures.dailyTemperatures(new int[] {73, 74, 75, 71, 69, 72, 76, 73}));
    assertArrayEquals(
        new int[] {0, 0, 0}, DailyTemperatures.dailyTemperatures(new int[] {80, 70, 60}));
  }
}
