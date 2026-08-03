package com.algoprep.patterns.linkedlist;

import com.algoprep.common.ListNode;

/** Linked-list reversal in groups; O(n) time and O(1) space. */
public final class ReverseEveryKElements {
  private ReverseEveryKElements() {}

  public static ListNode reverse(ListNode head, int k) {
    if (k <= 0) throw new IllegalArgumentException("k must be positive");
    if (k == 1 || head == null) return head;
    ListNode current = head, previous = null;
    while (current != null) {
      ListNode lastNodeOfPreviousPart = previous, lastNodeOfSubList = current;
      for (int i = 0; i < k && current != null; i++) {
        ListNode next = current.next;
        current.next = previous;
        previous = current;
        current = next;
      }
      if (lastNodeOfPreviousPart != null) lastNodeOfPreviousPart.next = previous;
      else head = previous;
      lastNodeOfSubList.next = current;
      previous = lastNodeOfSubList;
    }
    return head;
  }
}
