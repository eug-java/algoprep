package com.algoprep.patterns.fastslow;

import static org.junit.jupiter.api.Assertions.*;

import com.algoprep.common.ListNode;
import org.junit.jupiter.api.Test;

class LinkedListCycleTest {
  @Test
  void detectsCycle() {
    ListNode head = ListNode.of(1, 2, 3);
    head.next.next.next = head.next;
    assertTrue(LinkedListCycle.hasCycle(head));
  }

  @Test
  void rejectsAcyclicAndEmpty() {
    assertFalse(LinkedListCycle.hasCycle(ListNode.of(1)));
    assertFalse(LinkedListCycle.hasCycle(null));
  }
}
