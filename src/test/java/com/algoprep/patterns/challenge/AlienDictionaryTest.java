package com.algoprep.patterns.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AlienDictionaryTest {
  @Test
  void producesAnOrderThatSatisfiesAllConstraints() {
    String order = AlienDictionary.alienOrder(new String[] {"wrt", "wrf", "er", "ett", "rftt"});

    assertEquals(5, order.length());
    assertTrue(order.indexOf('w') < order.indexOf('e'));
    assertTrue(order.indexOf('e') < order.indexOf('r'));
    assertTrue(order.indexOf('r') < order.indexOf('t'));
    assertTrue(order.indexOf('t') < order.indexOf('f'));
  }

  @Test
  void rejectsPrefixViolationsAndCycles() {
    assertEquals("", AlienDictionary.alienOrder(new String[] {"abc", "ab"}));
    assertEquals("", AlienDictionary.alienOrder(new String[] {"z", "x", "z"}));
  }

  @Test
  void returnsTheOnlyCharacterWhenNoEdgesExist() {
    assertEquals("a", AlienDictionary.alienOrder(new String[] {"a"}));
  }
}
