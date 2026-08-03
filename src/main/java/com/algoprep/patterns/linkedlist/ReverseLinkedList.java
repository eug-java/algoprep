package com.algoprep.patterns.linkedlist;

import com.algoprep.common.ListNode;

/** In-place linked-list reversal; O(n) time and O(1) space. */
public final class ReverseLinkedList {
  private ReverseLinkedList() {}

  public static ListNode reverse(ListNode head) {
    ListNode previous = null;
    while (head != null) {
      ListNode next = head.next;
      head.next = previous;
      previous = head;
      head = next;
    }
    return previous;
  }
}
