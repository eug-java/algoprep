package com.algoprep.patterns.spring;

import java.util.concurrent.ConcurrentHashMap;

/** Thread-safe in-memory storage for string configuration values. */
public class ConfigStore {
  private final ConcurrentHashMap<String, String> values = new ConcurrentHashMap<>();

  /** Stores a non-null key/value pair. */
  public void set(String key, String value) {
    values.put(key, value);
  }

  /** Returns the value for key, or null when no value has been stored. */
  public String get(String key) {
    return values.get(key);
  }

  /** Returns the value for key, or defaultValue when key is absent. */
  public String getOrDefault(String key, String defaultValue) {
    return values.getOrDefault(key, defaultValue);
  }
}
