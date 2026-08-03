package com.algoprep.patterns.spring;

import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory analogue of a JPA {@code @Version} column: every record carries a version number,
 * and an update is only applied when the caller's expected version still matches the stored one.
 * A stale write - one based on data another writer has since changed - fails fast instead of
 * silently overwriting the newer value.
 */
public class OptimisticLockService<V> {
  private final ConcurrentHashMap<String, Record<V>> store = new ConcurrentHashMap<>();

  /** Inserts or fully replaces a record, resetting its version to 0. */
  public void put(String key, V value) {
    store.put(key, new Record<>(value, 0));
  }

  public V get(String key) {
    Record<V> record = store.get(key);
    if (record == null) throw new NoSuchElementException("no record for key " + key);
    return record.value;
  }

  public long versionOf(String key) {
    Record<V> record = store.get(key);
    if (record == null) throw new NoSuchElementException("no record for key " + key);
    return record.version;
  }

  /**
   * Updates {@code key} to {@code newValue} only if its current version equals
   * {@code expectedVersion}, then bumps the version. Throws {@link OptimisticLockException} if the
   * versions don't match, mirroring an {@code OptimisticLockException} from a JPA repository.
   */
  public synchronized void update(String key, V newValue, long expectedVersion) {
    Record<V> record = store.get(key);
    if (record == null) throw new NoSuchElementException("no record for key " + key);
    if (record.version != expectedVersion) {
      throw new OptimisticLockException(
          "expected version " + expectedVersion + " but was " + record.version + " for key " + key);
    }
    store.put(key, new Record<>(newValue, record.version + 1));
  }

  private record Record<V>(V value, long version) {}

  public static class OptimisticLockException extends RuntimeException {
    public OptimisticLockException(String message) {
      super(message);
    }
  }
}
