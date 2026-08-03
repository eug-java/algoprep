package com.algoprep.patterns.fastslow;

import com.algoprep.common.ListNode;

/** Fast and slow pointers; O(n) time and O(1) space. */
public final class LinkedListCycle {
  private LinkedListCycle() {}

  public static boolean hasCycle(ListNode head) {
    ListNode slow = head;
    ListNode fast = head;
    while (fast != null && fast.next != null) {
      slow = slow.next;
      fast = fast.next.next;
      if (slow == fast) return true;
    }
    return false;
  }
}
