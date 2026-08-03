package com.algoprep.patterns.backtracking;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;
import org.junit.jupiter.api.Test;

class LetterCasePermutationTest {
  @Test void permutesOnlyLetters() {
    assertEquals(
        Set.of("a1b2", "a1B2", "A1b2", "A1B2"),
        Set.copyOf(LetterCasePermutation.letterCasePermutation("a1b2")));
  }

  @Test void leavesDigitsUnchanged() {
    assertEquals(Set.of("123"), Set.copyOf(LetterCasePermutation.letterCasePermutation("123")));
  }
}
