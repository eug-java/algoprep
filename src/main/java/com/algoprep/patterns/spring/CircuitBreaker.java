package com.algoprep.patterns.spring;

import java.util.Objects;
import java.util.function.LongSupplier;

/** Minimal circuit breaker with closed, open, and half-open states. */
public class CircuitBreaker {
  public enum State { CLOSED, OPEN, HALF_OPEN }
  private final int failureThreshold; private final long openDurationMillis; private final LongSupplier clock; private int failures; private long openedAt; private State state=State.CLOSED;
  public CircuitBreaker(int failureThreshold,long openDurationMillis){this(failureThreshold,openDurationMillis,System::currentTimeMillis);}
  public CircuitBreaker(int failureThreshold,long openDurationMillis,LongSupplier clock){if(failureThreshold<=0||openDurationMillis<0)throw new IllegalArgumentException("invalid circuit breaker configuration");this.failureThreshold=failureThreshold;this.openDurationMillis=openDurationMillis;this.clock=Objects.requireNonNull(clock);}
  public synchronized boolean allowRequest(){if(state==State.OPEN){if(clock.getAsLong()-openedAt<openDurationMillis)return false;state=State.HALF_OPEN;}return true;}
  public synchronized void recordSuccess(){failures=0;state=State.CLOSED;}
  public synchronized void recordFailure(){if(state==State.HALF_OPEN||++failures>=failureThreshold){state=State.OPEN;openedAt=clock.getAsLong();}}
  public synchronized State getState(){return state;}
}
