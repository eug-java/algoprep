package com.algoprep.patterns.twopointers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class TripletSumToZeroTest {

  @Test
  void returnsUniqueTriplets() {
    assertEquals(
        List.of(List.of(-3, 1, 2), List.of(-2, 0, 2), List.of(-2, 1, 1), List.of(-1, 0, 1)),
        TripletSumToZero.searchTriplets(new int[] {-3, 0, 1, 2, -1, 1, -2}));
  }

  @Test
  void handlesTooShort() {
    assertTrue(TripletSumToZero.searchTriplets(new int[] {1, 2}).isEmpty());
  }
}
