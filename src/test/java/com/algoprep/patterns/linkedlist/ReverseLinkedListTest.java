package com.algoprep.patterns.linkedlist;

import static org.junit.jupiter.api.Assertions.*;

import com.algoprep.common.ListNode;
import java.util.*;
import org.junit.jupiter.api.Test;

class ReverseLinkedListTest {
  @Test
  void reversesList() {
    assertEquals(
        List.of(3, 2, 1), ListNode.toList(ReverseLinkedList.reverse(ListNode.of(1, 2, 3))));
  }

  @Test
  void handlesEmpty() {
    assertNull(ReverseLinkedList.reverse(null));
  }
}
