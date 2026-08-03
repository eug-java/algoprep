package com.algoprep.patterns.cyclicsort;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class FindAllDuplicatesTest {
  @Test
  void findsEveryDuplicatedValue() {
    assertEquals(List.of(3, 2), FindAllDuplicates.findNumbers(new int[] {4, 3, 2, 7, 8, 2, 3, 1}));
    assertEquals(List.of(), FindAllDuplicates.findNumbers(new int[] {1, 2, 3}));
  }
}
