package com.algoprep.patterns.tracking;

/** Finds the element occurring more than half the time. */
public final class MajorityElement {
  private MajorityElement() {}

  public static int findMajority(int[] nums) {
    int candidate = 0, votes = 0;
    for (int n : nums) {
      if (votes == 0) candidate = n;
      votes += n == candidate ? 1 : -1;
    }
    return candidate;
  }
}
