package com.algoprep.patterns.subsets;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class CombinationsOfSizeKTest {
  @Test void generatesAllCombinationsInAscendingOrder() {
    assertEquals(
        List.of(List.of(1, 2), List.of(1, 3), List.of(1, 4), List.of(2, 3), List.of(2, 4), List.of(3, 4)),
        CombinationsOfSizeK.combine(4, 2));
  }

  @Test void handlesZeroAndImpossibleSizes() {
    assertEquals(List.of(List.of()), CombinationsOfSizeK.combine(3, 0));
    assertTrue(CombinationsOfSizeK.combine(2, 3).isEmpty());
  }
}
