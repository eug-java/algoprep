package com.algoprep.patterns.spring;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class OptimisticLockServiceTest {
  @Test
  void updateWithCorrectVersionSucceedsAndBumpsVersion() {
    OptimisticLockService<String> service = new OptimisticLockService<>();
    service.put("acct-1", "balance:100");

    service.update("acct-1", "balance:150", 0);

    assertEquals("balance:150", service.get("acct-1"));
    assertEquals(1, service.versionOf("acct-1"));
  }

  @Test
  void updateWithStaleVersionThrows() {
    OptimisticLockService<String> service = new OptimisticLockService<>();
    service.put("acct-1", "balance:100");
    service.update("acct-1", "balance:150", 0);

    assertThrows(OptimisticLockService.OptimisticLockException.class, () ->
        service.update("acct-1", "balance:200", 0));
    assertEquals("balance:150", service.get("acct-1"));
  }

  @Test
  void updateOnMissingKeyThrows() {
    OptimisticLockService<String> service = new OptimisticLockService<>();
    assertThrows(NoSuchElementException.class, () -> service.update("missing", "x", 0));
  }

  @Test
  void putResetsVersionToZero() {
    OptimisticLockService<String> service = new OptimisticLockService<>();
    service.put("k", "v1");
    service.update("k", "v2", 0);
    service.put("k", "v3");

    assertEquals(0, service.versionOf("k"));
    assertEquals("v3", service.get("k"));
  }
}
