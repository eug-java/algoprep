package com.algoprep.patterns.spring;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class ConfigStoreTest {
  @Test void storesAndReadsValuesWithFallback() {
    ConfigStore store = new ConfigStore();
    store.set("theme", "dark");
    assertEquals("dark", store.get("theme"));
    assertEquals("light", store.getOrDefault("missing", "light"));
  }

  @Test void acceptsConcurrentWrites() throws InterruptedException {
    ConfigStore store = new ConfigStore();
    try (var executor = Executors.newFixedThreadPool(4)) {
      for (int i = 0; i < 100; i++) {
        int index = i;
        executor.submit(() -> store.set("key-" + index, Integer.toString(index)));
      }
      executor.shutdown();
      assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
    for (int i = 0; i < 100; i++) assertEquals(Integer.toString(i), store.get("key-" + i));
  }
}
