package com.algoprep.patterns.customds;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DesignHashMapTest {
  @Test void putsUpdatesGetsAndRemovesValues() {
    DesignHashMap map = new DesignHashMap();
    map.put(1, 10);
    map.put(1, 20);
    assertEquals(20, map.get(1));
    map.remove(1);
    assertEquals(-1, map.get(1));
  }

  @Test void supportsCollidingAndNegativeKeys() {
    DesignHashMap map = new DesignHashMap();
    map.put(1, 1);
    map.put(770, 2);
    map.put(-1, 3);
    map.remove(1);
    assertEquals(2, map.get(770));
    assertEquals(3, map.get(-1));
  }
}
