package com.algoprep.patterns.kwaymerge;

import com.algoprep.common.ListNode;

/** Merges two ascending singly linked lists. */
public final class MergeTwoSortedLists {
  private MergeTwoSortedLists() {}

  public static ListNode mergeTwoLists(ListNode l1, ListNode l2) {
    ListNode dummy = new ListNode();
    ListNode tail = dummy;
    while (l1 != null && l2 != null) {
      if (l1.val <= l2.val) {
        tail.next = l1;
        l1 = l1.next;
      } else {
        tail.next = l2;
        l2 = l2.next;
      }
      tail = tail.next;
    }
    tail.next = l1 != null ? l1 : l2;
    return dummy.next;
  }
}
