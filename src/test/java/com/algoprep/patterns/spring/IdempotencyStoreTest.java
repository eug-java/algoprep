package com.algoprep.patterns.spring;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.Test;

class IdempotencyStoreTest {
  @Test
  void returnsStoredResponseBeforeExpiry() {
    IdempotencyStore store = new IdempotencyStore(Duration.ofSeconds(1));
    store.put("request", "response");
    assertEquals("response", store.get("request").orElseThrow());
  }

  @Test
  void expiresEntries() throws InterruptedException {
    IdempotencyStore store = new IdempotencyStore(Duration.ofMillis(1));
    store.put("request", "response");
    Thread.sleep(5);
    assertTrue(store.get("request").isEmpty());
  }
}
