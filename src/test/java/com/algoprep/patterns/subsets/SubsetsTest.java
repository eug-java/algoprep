package com.algoprep.patterns.subsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class SubsetsTest {
  @Test
  void findsAllSubsetsAndHandlesEmptyInput() {
    assertEquals(
        List.of(List.of(), List.of(1), List.of(2), List.of(1, 2)),
        Subsets.findSubsets(new int[] {1, 2}));
    assertEquals(List.of(List.of()), Subsets.findSubsets(new int[] {}));
  }
}
