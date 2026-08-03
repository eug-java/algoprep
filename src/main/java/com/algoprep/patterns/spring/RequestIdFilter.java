package com.algoprep.patterns.spring;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Resolves and propagates a correlation id (the classic {@code X-Request-Id} header) for an
 * inbound request: reuse the caller-supplied id when present, otherwise generate a fresh one, and
 * make it available to the rest of the request via a thread-local so downstream code and logging
 * don't need it threaded through every method signature.
 */
public class RequestIdFilter {
  public static final String HEADER_NAME = "X-Request-Id";

  private final Supplier<String> idGenerator;
  private final ThreadLocal<String> current = new ThreadLocal<>();

  public RequestIdFilter() {
    this(() -> UUID.randomUUID().toString());
  }

  public RequestIdFilter(Supplier<String> idGenerator) {
    if (idGenerator == null) throw new IllegalArgumentException("idGenerator is required");
    this.idGenerator = idGenerator;
  }

  /**
   * Resolves the request id for an inbound request: an existing, non-blank incoming header value
   * is reused as-is; otherwise a new id is generated. The resolved id becomes the current id for
   * this thread until {@link #clear()} is called.
   */
  public String resolve(String incomingHeaderValue) {
    String id = (incomingHeaderValue == null || incomingHeaderValue.isBlank())
        ? idGenerator.get()
        : incomingHeaderValue.trim();
    current.set(id);
    return id;
  }

  /** The request id resolved on this thread, or {@code null} if none has been resolved yet. */
  public String current() {
    return current.get();
  }

  /** Clears the thread-local id; call this once the request finishes to avoid leaking state. */
  public void clear() {
    current.remove();
  }
}
