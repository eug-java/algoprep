package com.algoprep.patterns.spring;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Concurrent response store that expires entries after a TTL. */
public class IdempotencyStore {
  private final long ttlNanos;
  private final ConcurrentHashMap<String, Entry> entries = new ConcurrentHashMap<>();

  public IdempotencyStore(Duration ttl) {
    if (ttl == null || ttl.isNegative() || ttl.isZero())
      throw new IllegalArgumentException("ttl must be positive");
    ttlNanos = ttl.toNanos();
  }

  public Optional<String> get(String key) {
    Entry entry = entries.get(key);
    if (entry == null) return Optional.empty();
    if (System.nanoTime() - entry.createdAtNanos >= ttlNanos) {
      entries.remove(key, entry);
      return Optional.empty();
    }
    return Optional.of(entry.response);
  }

  public void put(String key, String response) {
    entries.put(key, new Entry(response, System.nanoTime()));
  }

  private record Entry(String response, long createdAtNanos) {}
}
