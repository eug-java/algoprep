package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PalindromePairsTest {
  @Test
  void findsPairs() {
    List<List<Integer>> result = PalindromePairs.palindromePairs(new String[] {"abcd", "dcba", "lls", "s", "sssll"});
    Set<String> normalized = new HashSet<>();
    for (List<Integer> pair : result) normalized.add(pair.get(0) + "," + pair.get(1));
    assertTrue(normalized.contains("0,1"));
    assertTrue(normalized.contains("1,0"));
    assertTrue(normalized.contains("3,2"));
    assertTrue(normalized.contains("2,4"));
    assertEquals(4, normalized.size());
  }
}
