package com.algoprep.patterns.twoheaps;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class MaximizeCapitalTest {
  @Test
  void choosesBestFeasibleProjects() {
    assertEquals(
        6, MaximizeCapital.findMaximumCapital(new int[] {0, 1, 2}, new int[] {1, 2, 3}, 3, 0));
  }

  @Test
  void stopsWhenNothingIsFeasible() {
    assertEquals(0, MaximizeCapital.findMaximumCapital(new int[] {1}, new int[] {5}, 2, 0));
  }
}
