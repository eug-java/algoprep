package com.algoprep.patterns.twopointers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class RemoveDuplicatesTest {
  @Test
  void compactsSortedValues() {
    int[] values = {2, 3, 3, 3, 6, 9, 9};
    assertEquals(4, RemoveDuplicates.remove(values));
    assertArrayEquals(new int[] {2, 3, 6, 9}, java.util.Arrays.copyOf(values, 4));
  }

  @Test
  void handlesEmpty() {
    assertEquals(0, RemoveDuplicates.remove(new int[] {}));
  }
}
