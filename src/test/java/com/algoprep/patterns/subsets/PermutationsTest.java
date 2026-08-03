package com.algoprep.patterns.subsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class PermutationsTest {
  @Test
  void findsEveryOrdering() {
    List<List<Integer>> permutations = Permutations.findPermutations(new int[] {1, 2, 3});
    assertEquals(6, permutations.size());
    assertTrue(
        permutations.containsAll(
            List.of(
                List.of(1, 2, 3),
                List.of(1, 3, 2),
                List.of(2, 1, 3),
                List.of(2, 3, 1),
                List.of(3, 1, 2),
                List.of(3, 2, 1))));
  }

  @Test
  void emptyInputHasOneEmptyPermutation() {
    assertEquals(List.of(List.of()), Permutations.findPermutations(new int[] {}));
  }
}
