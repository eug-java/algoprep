package com.algoprep.patterns.slidingwindow;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.Test;

class FindAllAnagramsTest {
  @Test
  void findsOverlappingAnagrams() {
    assertEquals(List.of(0, 6), FindAllAnagrams.findAnagrams("cbaebabacd", "abc"));
    assertEquals(List.of(0, 1, 2), FindAllAnagrams.findAnagrams("abab", "ab"));
  }

  @Test
  void handlesEmptyInput() {
    assertTrue(FindAllAnagrams.findAnagrams("a", "").isEmpty());
  }
}
