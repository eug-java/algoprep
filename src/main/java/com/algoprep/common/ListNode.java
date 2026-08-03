package com.algoprep.common;

import java.util.ArrayList;
import java.util.List;

/** Singly linked list node used across linked-list patterns. */
public class ListNode {
  public int val;
  public ListNode next;

  public ListNode() {}

  public ListNode(int val) {
    this.val = val;
  }

  public ListNode(int val, ListNode next) {
    this.val = val;
    this.next = next;
  }

  public static ListNode of(int... values) {
    ListNode dummy = new ListNode(0);
    ListNode cur = dummy;
    for (int v : values) {
      cur.next = new ListNode(v);
      cur = cur.next;
    }
    return dummy.next;
  }

  public static List<Integer> toList(ListNode head) {
    List<Integer> out = new ArrayList<>();
    while (head != null) {
      out.add(head.val);
      head = head.next;
    }
    return out;
  }

  @Override
  public String toString() {
    return toList(this).toString();
  }
}
