package com.algoprep.patterns.fastslow;

import com.algoprep.common.ListNode;

/** Locates the first node of a linked-list cycle. */
public final class CycleStart {
  private CycleStart() {}

  public static ListNode findCycleStart(ListNode head) {
    ListNode slow = head, fast = head;
    while (fast != null && fast.next != null) {
      slow = slow.next; fast = fast.next.next;
      if (slow == fast) {
        slow = head;
        while (slow != fast) { slow = slow.next; fast = fast.next; }
        return slow;
      }
    }
    return null;
  }
}
