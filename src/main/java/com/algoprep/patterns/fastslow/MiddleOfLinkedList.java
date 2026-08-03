package com.algoprep.patterns.fastslow;

import com.algoprep.common.ListNode;

/** Fast and slow pointers; O(n) time and O(1) space. */
public final class MiddleOfLinkedList {
  private MiddleOfLinkedList() {}

  public static ListNode findMiddle(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
      slow = slow.next;
      fast = fast.next.next;
    }
    return slow;
  }
}
