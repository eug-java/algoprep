package com.algoprep.patterns.linkedlist;

import com.algoprep.common.ListNode;

/** Reorders a list as first, last, second, second-last, and so on. */
public final class ReorderList {
  private ReorderList() {}
  public static void reorderList(ListNode head) {
    if (head == null || head.next == null) return;
    ListNode slow = head, fast = head;
    while (fast.next != null && fast.next.next != null) { slow = slow.next; fast = fast.next.next; }
    ListNode second = reverse(slow.next); slow.next = null;
    ListNode first = head;
    while (second != null) {
      ListNode nextFirst = first.next, nextSecond = second.next;
      first.next = second; second.next = nextFirst;
      first = nextFirst; second = nextSecond;
    }
  }
  private static ListNode reverse(ListNode head) {
    ListNode previous = null;
    while (head != null) { ListNode next = head.next; head.next = previous; previous = head; head = next; }
    return previous;
  }
}
