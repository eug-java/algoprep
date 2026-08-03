package com.algoprep.patterns.spring;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TokenBucketRateLimiterTest {
  @Test
  void enforcesCapacityIndependentlyPerKey() {
    TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(2, 0);
    assertTrue(limiter.allow("one"));
    assertTrue(limiter.allow("one"));
    assertFalse(limiter.allow("one"));
    assertTrue(limiter.allow("two"));
  }
}
