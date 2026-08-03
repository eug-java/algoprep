package com.algoprep.patterns.linkedlist;

import static org.junit.jupiter.api.Assertions.*;

import com.algoprep.common.ListNode;
import java.util.*;
import org.junit.jupiter.api.Test;

class ReverseSubListTest {
  @Test
  void reversesInclusiveRange() {
    assertEquals(
        List.of(1, 4, 3, 2, 5),
        ListNode.toList(ReverseSubList.reverse(ListNode.of(1, 2, 3, 4, 5), 2, 4)));
  }

  @Test
  void validatesPositions() {
    assertThrows(
        IllegalArgumentException.class, () -> ReverseSubList.reverse(ListNode.of(1), 0, 1));
  }
}
