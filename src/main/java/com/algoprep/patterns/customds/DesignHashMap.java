package com.algoprep.patterns.customds;

/** A small integer-to-integer hash map implemented with separate chaining. */
public class DesignHashMap {
  private static final int BUCKET_COUNT = 769;
  private final Entry[] buckets = new Entry[BUCKET_COUNT];

  /** Associates value with key, replacing the prior value when present. */
  public void put(int key, int value) {
    int bucket = bucketIndex(key);
    for (Entry entry = buckets[bucket]; entry != null; entry = entry.next) {
      if (entry.key == key) {
        entry.value = value;
        return;
      }
    }
    buckets[bucket] = new Entry(key, value, buckets[bucket]);
  }

  /** Returns the value associated with key, or -1 when it is absent. */
  public int get(int key) {
    for (Entry entry = buckets[bucketIndex(key)]; entry != null; entry = entry.next) {
      if (entry.key == key) return entry.value;
    }
    return -1;
  }

  /** Removes key and its associated value when present. */
  public void remove(int key) {
    int bucket = bucketIndex(key);
    Entry previous = null;
    Entry current = buckets[bucket];
    while (current != null) {
      if (current.key == key) {
        if (previous == null) buckets[bucket] = current.next;
        else previous.next = current.next;
        return;
      }
      previous = current;
      current = current.next;
    }
  }

  private int bucketIndex(int key) {
    return Math.floorMod(key, BUCKET_COUNT);
  }

  private static final class Entry {
    private final int key;
    private int value;
    private Entry next;

    private Entry(int key, int value, Entry next) {
      this.key = key;
      this.value = value;
      this.next = next;
    }
  }
}
