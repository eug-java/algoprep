package com.algoprep.patterns.sortsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class GroupAnagramsTest {
  @Test
  void groupsAnagramsInEncounterOrder() {
    assertEquals(
        List.of(List.of("eat", "tea", "ate"), List.of("tan", "nat"), List.of("bat")),
        GroupAnagrams.group(new String[] {"eat", "tea", "tan", "ate", "nat", "bat"}));
    assertEquals(List.of(List.of("")), GroupAnagrams.group(new String[] {""}));
  }
}
