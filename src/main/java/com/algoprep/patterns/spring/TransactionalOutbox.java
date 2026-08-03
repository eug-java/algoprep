package com.algoprep.patterns.spring;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * In-memory model of the transactional outbox pattern: an event is enqueued in the same
 * "transaction" as the business write, and a separate step later publishes only the events that
 * are due, marking each as published so a retry of {@link #publishDue} never re-delivers it.
 */
public class TransactionalOutbox {
  public enum Status { PENDING, PUBLISHED }

  private final List<Entry> entries = new ArrayList<>();
  private final AtomicLong nextId = new AtomicLong(1);

  /** Enqueues a payload that becomes eligible for publishing immediately. */
  public synchronized long enqueue(String payload) {
    return enqueue(payload, Instant.EPOCH);
  }

  /** Enqueues a payload that only becomes eligible for publishing at {@code eligibleAt}. */
  public synchronized long enqueue(String payload, Instant eligibleAt) {
    if (payload == null) throw new IllegalArgumentException("payload is required");
    if (eligibleAt == null) throw new IllegalArgumentException("eligibleAt is required");
    long id = nextId.getAndIncrement();
    entries.add(new Entry(id, payload, eligibleAt));
    return id;
  }

  /**
   * Publishes every PENDING event whose {@code eligibleAt} is not after {@code now}, in enqueue
   * order. Each event is marked PUBLISHED only after the publisher returns normally, so if the
   * publisher throws, that event (and any not yet attempted) stays PENDING for a later retry.
   */
  public synchronized List<String> publishDue(Instant now, Consumer<String> publisher) {
    if (now == null) throw new IllegalArgumentException("now is required");
    if (publisher == null) throw new IllegalArgumentException("publisher is required");
    List<String> published = new ArrayList<>();
    for (Entry entry : entries) {
      if (entry.status == Status.PENDING && !entry.eligibleAt.isAfter(now)) {
        publisher.accept(entry.payload);
        entry.status = Status.PUBLISHED;
        published.add(entry.payload);
      }
    }
    return published;
  }

  public synchronized long pendingCount() {
    return entries.stream().filter(e -> e.status == Status.PENDING).count();
  }

  public synchronized Status statusOf(long id) {
    return entries.stream()
        .filter(e -> e.id == id)
        .findFirst()
        .map(e -> e.status)
        .orElseThrow(() -> new java.util.NoSuchElementException("no outbox entry with id " + id));
  }

  private static final class Entry {
    final long id;
    final String payload;
    final Instant eligibleAt;
    volatile Status status = Status.PENDING;

    Entry(long id, String payload, Instant eligibleAt) {
      this.id = id;
      this.payload = payload;
      this.eligibleAt = eligibleAt;
    }
  }
}
