package com.algoprep.patterns.spring;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class ConcurrentLoginTrackerTest {
  @Test
  void rejectsNewSessionPastLimitUnderRejectPolicy() {
    ConcurrentLoginTracker tracker =
        new ConcurrentLoginTracker(2, ConcurrentLoginTracker.Policy.REJECT_NEW);
    assertTrue(tracker.login("u1", "s1"));
    assertTrue(tracker.login("u1", "s2"));
    assertFalse(tracker.login("u1", "s3"));
    assertEquals(2, tracker.activeCount("u1"));
  }

  @Test
  void evictsOldestSessionUnderEvictPolicy() {
    ConcurrentLoginTracker tracker =
        new ConcurrentLoginTracker(2, ConcurrentLoginTracker.Policy.EVICT_OLDEST);
    tracker.login("u1", "s1");
    tracker.login("u1", "s2");
    assertTrue(tracker.login("u1", "s3"));

    assertEquals(2, tracker.activeCount("u1"));
    assertEquals(java.util.Set.of("s2", "s3"), tracker.activeSessions("u1"));
  }

  @Test
  void logoutFreesUpASlot() {
    ConcurrentLoginTracker tracker =
        new ConcurrentLoginTracker(1, ConcurrentLoginTracker.Policy.REJECT_NEW);
    tracker.login("u1", "s1");
    assertFalse(tracker.login("u1", "s2"));

    tracker.logout("u1", "s1");
    assertTrue(tracker.login("u1", "s2"));
  }

  @Test
  void tracksUsersIndependently() {
    ConcurrentLoginTracker tracker =
        new ConcurrentLoginTracker(1, ConcurrentLoginTracker.Policy.REJECT_NEW);
    assertTrue(tracker.login("u1", "s1"));
    assertTrue(tracker.login("u2", "s1"));
    assertEquals(1, tracker.activeCount("u1"));
    assertEquals(1, tracker.activeCount("u2"));
  }

  @Test
  void neverExceedsLimitUnderConcurrentLogins() throws InterruptedException {
    int max = 3;
    ConcurrentLoginTracker tracker =
        new ConcurrentLoginTracker(max, ConcurrentLoginTracker.Policy.REJECT_NEW);
    int attempts = 50;
    ExecutorService pool = Executors.newFixedThreadPool(10);
    CountDownLatch start = new CountDownLatch(1);
    CountDownLatch done = new CountDownLatch(attempts);
    AtomicInteger accepted = new AtomicInteger();

    for (int i = 0; i < attempts; i++) {
      int sessionIndex = i;
      pool.submit(() -> {
        try {
          start.await();
          if (tracker.login("busy-user", "session-" + sessionIndex)) {
            accepted.incrementAndGet();
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        } finally {
          done.countDown();
        }
      });
    }

    start.countDown();
    assertTrue(done.await(10, TimeUnit.SECONDS));
    pool.shutdown();

    assertTrue(tracker.activeCount("busy-user") <= max);
    assertEquals(max, accepted.get());
  }
}
