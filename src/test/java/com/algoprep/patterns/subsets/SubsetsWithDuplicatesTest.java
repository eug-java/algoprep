package com.algoprep.patterns.subsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class SubsetsWithDuplicatesTest {
  @Test
  void omitsDuplicateSubsets() {
    assertEquals(
        List.of(List.of(), List.of(1), List.of(2), List.of(1, 2), List.of(2, 2), List.of(1, 2, 2)),
        SubsetsWithDuplicates.findSubsets(new int[] {1, 2, 2}));
  }
}
