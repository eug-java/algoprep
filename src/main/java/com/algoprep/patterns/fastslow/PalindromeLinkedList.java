package com.algoprep.patterns.fastslow;

import com.algoprep.common.ListNode;

/** Checks whether linked-list values read identically forwards and backwards. */
public final class PalindromeLinkedList {
  private PalindromeLinkedList() {}

  public static boolean isPalindrome(ListNode head) {
    if (head == null || head.next == null) return true;
    ListNode slow = head, fast = head;
    while (fast.next != null && fast.next.next != null) { slow = slow.next; fast = fast.next.next; }
    ListNode second = reverse(slow.next), saved = second;
    boolean palindrome = true;
    for (ListNode first = head; second != null; first = first.next, second = second.next) {
      if (first.val != second.val) { palindrome = false; break; }
    }
    slow.next = reverse(saved);
    return palindrome;
  }

  private static ListNode reverse(ListNode head) {
    ListNode previous = null;
    while (head != null) { ListNode next = head.next; head.next = previous; previous = head; head = next; }
    return previous;
  }
}
