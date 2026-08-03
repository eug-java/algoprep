package com.algoprep.common;

/** Inclusive interval [start, end]. */
public record Interval(int start, int end) implements Comparable<Interval> {

  @Override
  public int compareTo(Interval o) {
    return Integer.compare(this.start, o.start);
  }
}
