package com.algoprep.patterns.spring;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enforces a "max N concurrent sessions per user" policy, the kind of question that shows up when
 * an interviewer asks how you'd stop the same account from being logged in on too many devices at
 * once. Sessions are tracked in insertion order per user so the oldest session is easy to find;
 * each user's session set is only ever mutated while holding a lock on that same set instance,
 * which gives per-user (not global) mutual exclusion.
 */
public class ConcurrentLoginTracker {
  public enum Policy { REJECT_NEW, EVICT_OLDEST }

  private final int maxSessionsPerUser;
  private final Policy policy;
  private final ConcurrentHashMap<String, LinkedHashSet<String>> sessionsByUser = new ConcurrentHashMap<>();

  public ConcurrentLoginTracker(int maxSessionsPerUser, Policy policy) {
    if (maxSessionsPerUser <= 0) throw new IllegalArgumentException("maxSessionsPerUser must be positive");
    if (policy == null) throw new IllegalArgumentException("policy is required");
    this.maxSessionsPerUser = maxSessionsPerUser;
    this.policy = policy;
  }

  /**
   * Attempts to register {@code sessionId} as an active session for {@code userId}. Returns
   * {@code true} if the session is now active (including re-logging in an already-active
   * session). Returns {@code false} only under {@link Policy#REJECT_NEW} when the user is already
   * at the session limit and this is a brand-new session.
   */
  public boolean login(String userId, String sessionId) {
    LinkedHashSet<String> sessions = sessionsByUser.computeIfAbsent(userId, ignored -> new LinkedHashSet<>());
    synchronized (sessions) {
      if (sessions.contains(sessionId)) {
        sessions.remove(sessionId);
        sessions.add(sessionId);
        return true;
      }
      if (sessions.size() >= maxSessionsPerUser) {
        if (policy == Policy.REJECT_NEW) return false;
        sessions.remove(sessions.iterator().next());
      }
      sessions.add(sessionId);
      return true;
    }
  }

  public void logout(String userId, String sessionId) {
    LinkedHashSet<String> sessions = sessionsByUser.get(userId);
    if (sessions == null) return;
    synchronized (sessions) {
      sessions.remove(sessionId);
    }
  }

  public Set<String> activeSessions(String userId) {
    LinkedHashSet<String> sessions = sessionsByUser.get(userId);
    if (sessions == null) return Set.of();
    synchronized (sessions) {
      return new LinkedHashSet<>(sessions);
    }
  }

  public int activeCount(String userId) {
    LinkedHashSet<String> sessions = sessionsByUser.get(userId);
    if (sessions == null) return 0;
    synchronized (sessions) {
      return sessions.size();
    }
  }
}
