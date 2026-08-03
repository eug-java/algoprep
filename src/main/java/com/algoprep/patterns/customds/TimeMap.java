package com.algoprep.patterns.customds;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Stores timestamped values and retrieves their latest prior version. */
public class TimeMap {
  private final Map<String, TreeMap<Integer,String>> values = new HashMap<>();
  public TimeMap() {}
  public void set(String key, String value, int timestamp) { values.computeIfAbsent(key, ignored -> new TreeMap<>()).put(timestamp, value); }
  public String get(String key, int timestamp) { Map.Entry<Integer,String> entry=values.getOrDefault(key,new TreeMap<>()).floorEntry(timestamp); return entry==null ? "" : entry.getValue(); }
}
