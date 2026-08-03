package com.algoprep.patterns.linkedlist;

import com.algoprep.common.ListNode;

/** In-place linked-list sublist reversal; O(n) time and O(1) space. */
public final class ReverseSubList {
  private ReverseSubList() {}

  public static ListNode reverse(ListNode head, int p, int q) {
    if (p < 1 || q < p) throw new IllegalArgumentException("invalid positions");
    ListNode dummy = new ListNode(0, head), before = dummy;
    for (int i = 1; i < p; i++) {
      if (before.next == null) throw new IllegalArgumentException("position outside list");
      before = before.next;
    }
    ListNode current = before.next, previous = null;
    for (int i = p; i <= q; i++) {
      if (current == null) throw new IllegalArgumentException("position outside list");
      ListNode next = current.next;
      current.next = previous;
      previous = current;
      current = next;
    }
    ListNode tail = before.next;
    before.next = previous;
    tail.next = current;
    return dummy.next;
  }
}
