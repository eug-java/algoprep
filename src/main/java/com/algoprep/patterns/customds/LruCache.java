package com.algoprep.patterns.customds;

import java.util.LinkedHashMap;

/** Fixed-capacity least-recently-used integer cache. */
public class LruCache {
  private final int capacity;
  private final LinkedHashMap<Integer, Integer> values = new LinkedHashMap<>(16, .75f, true);

  public LruCache(int capacity) {
    if (capacity < 0) throw new IllegalArgumentException("capacity must be non-negative");
    this.capacity = capacity;
  }

  public int get(int key) {
    return values.getOrDefault(key, -1);
  }

  public void put(int key, int value) {
    values.put(key, value);
    if (values.size() > capacity) {
      var iterator = values.entrySet().iterator();
      iterator.next();
      iterator.remove();
    }
  }
}
