package com.algoprep.patterns.greedy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AssignCookiesTest {
  @Test void maximizesContentChildren() {
    assertEquals(1, AssignCookies.findContentChildren(new int[] {1, 2, 3}, new int[] {1, 1}));
    assertEquals(2, AssignCookies.findContentChildren(new int[] {1, 2}, new int[] {1, 2, 3}));
  }

  @Test void doesNotMutateCallerArrays() {
    int[] greed = {2, 1};
    int[] cookies = {1, 2};
    assertEquals(2, AssignCookies.findContentChildren(greed, cookies));
    assertArrayEquals(new int[] {2, 1}, greed);
    assertArrayEquals(new int[] {1, 2}, cookies);
  }
}
