package com.algoprep.patterns.sortsearch;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class IntersectionTwoArraysTest {
  @Test void returnsEachSharedValueOnce() {
    int[] result = IntersectionTwoArrays.intersection(new int[] {4, 9, 5, 9}, new int[] {9, 4, 9, 8, 4});
    Arrays.sort(result);
    assertArrayEquals(new int[] {4, 9}, result);
  }

  @Test void returnsEmptyArrayWhenNoValuesOverlap() {
    assertArrayEquals(new int[] {}, IntersectionTwoArrays.intersection(new int[] {1, 2}, new int[] {3, 4}));
  }
}
