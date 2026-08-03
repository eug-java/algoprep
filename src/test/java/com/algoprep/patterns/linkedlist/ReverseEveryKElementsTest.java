package com.algoprep.patterns.linkedlist;

import static org.junit.jupiter.api.Assertions.*;

import com.algoprep.common.ListNode;
import java.util.*;
import org.junit.jupiter.api.Test;

class ReverseEveryKElementsTest {
  @Test
  void reversesFullAndPartialGroups() {
    assertEquals(
        List.of(2, 1, 4, 3, 5),
        ListNode.toList(ReverseEveryKElements.reverse(ListNode.of(1, 2, 3, 4, 5), 2)));
  }

  @Test
  void handlesSingleAndRejectsZero() {
    assertEquals(List.of(1), ListNode.toList(ReverseEveryKElements.reverse(ListNode.of(1), 1)));
    assertThrows(IllegalArgumentException.class, () -> ReverseEveryKElements.reverse(null, 0));
  }
}
