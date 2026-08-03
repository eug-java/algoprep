package com.algoprep.patterns.spring;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class TransactionalOutboxTest {
  @Test
  void publishesOnlyEventsThatAreDue() {
    TransactionalOutbox outbox = new TransactionalOutbox();
    Instant now = Instant.parse("2024-01-01T00:00:00Z");
    outbox.enqueue("due-now", now.minusSeconds(1));
    outbox.enqueue("not-yet", now.plusSeconds(60));

    List<String> published = outbox.publishDue(now, event -> {});

    assertEquals(List.of("due-now"), published);
    assertEquals(1, outbox.pendingCount());
  }

  @Test
  void doesNotRepublishAlreadyPublishedEvents() {
    TransactionalOutbox outbox = new TransactionalOutbox();
    Instant now = Instant.parse("2024-01-01T00:00:00Z");
    long id = outbox.enqueue("payload");

    outbox.publishDue(now, event -> {});
    List<String> secondRun = outbox.publishDue(now, event -> {});

    assertTrue(secondRun.isEmpty());
    assertEquals(TransactionalOutbox.Status.PUBLISHED, outbox.statusOf(id));
  }

  @Test
  void leavesEventPendingWhenPublisherThrows() {
    TransactionalOutbox outbox = new TransactionalOutbox();
    long id = outbox.enqueue("payload");

    assertThrows(RuntimeException.class, () ->
        outbox.publishDue(Instant.now(), event -> {
          throw new RuntimeException("downstream unavailable");
        }));

    assertEquals(TransactionalOutbox.Status.PENDING, outbox.statusOf(id));
    assertEquals(1, outbox.pendingCount());
  }

  @Test
  void publishesInEnqueueOrder() {
    TransactionalOutbox outbox = new TransactionalOutbox();
    outbox.enqueue("first");
    outbox.enqueue("second");

    List<String> published = outbox.publishDue(Instant.now(), event -> {});

    assertEquals(List.of("first", "second"), published);
  }
}
