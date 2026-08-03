package com.algoprep.patterns.fastslow;

import static org.junit.jupiter.api.Assertions.*;

import com.algoprep.common.ListNode;
import org.junit.jupiter.api.Test;

class MiddleOfLinkedListTest {
  @Test
  void returnsSecondMiddleForEvenLength() {
    assertEquals(4, MiddleOfLinkedList.findMiddle(ListNode.of(1, 2, 3, 4, 5, 6)).val);
  }

  @Test
  void handlesSingleAndEmpty() {
    assertEquals(1, MiddleOfLinkedList.findMiddle(ListNode.of(1)).val);
    assertNull(MiddleOfLinkedList.findMiddle(null));
  }
}
