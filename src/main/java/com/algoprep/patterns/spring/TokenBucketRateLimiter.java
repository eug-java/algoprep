package com.algoprep.patterns.spring;

import java.util.concurrent.ConcurrentHashMap;

/** Thread-safe per-key token bucket rate limiter. */
public class TokenBucketRateLimiter {
  private final int capacity;
  private final double refillPerSecond;
  private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

  public TokenBucketRateLimiter(int capacity, double refillPerSecond) {
    if (capacity <= 0 || refillPerSecond < 0)
      throw new IllegalArgumentException("invalid bucket configuration");
    this.capacity = capacity;
    this.refillPerSecond = refillPerSecond;
  }

  public boolean allow(String key) {
    return buckets.computeIfAbsent(key, ignored -> new Bucket(capacity)).tryConsume();
  }

  private final class Bucket {
    private double tokens;
    private long lastRefillNanos;

    private Bucket(int capacity) {
      tokens = capacity;
      lastRefillNanos = System.nanoTime();
    }

    private synchronized boolean tryConsume() {
      long now = System.nanoTime();
      tokens =
          Math.min(capacity, tokens + (now - lastRefillNanos) / 1_000_000_000d * refillPerSecond);
      lastRefillNanos = now;
      if (tokens < 1) return false;
      tokens--;
      return true;
    }
  }
}
