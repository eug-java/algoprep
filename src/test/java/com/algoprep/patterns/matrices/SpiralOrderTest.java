package com.algoprep.patterns.matrices;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class SpiralOrderTest {
  @Test
  void traversesSquareAndSingleRowMatrices() {
    assertEquals(
        List.of(1, 2, 3, 6, 9, 8, 7, 4, 5),
        SpiralOrder.spiralOrder(new int[][] {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}));
    assertEquals(List.of(1, 2, 3), SpiralOrder.spiralOrder(new int[][] {{1, 2, 3}}));
    assertEquals(List.of(), SpiralOrder.spiralOrder(new int[][] {}));
  }
}
