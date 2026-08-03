package com.algoprep.patterns.kwaymerge;

import com.algoprep.common.ListNode;
import java.util.*;

/** K-way merge with a min-heap; O(n log k) time and O(k) space. */
public final class MergeKSortedLists {
  private MergeKSortedLists() {}

  public static ListNode merge(ListNode[] lists) {
    if (lists == null || lists.length == 0) return null;
    PriorityQueue<ListNode> heap = new PriorityQueue<>(Comparator.comparingInt(node -> node.val));
    for (ListNode node : lists) if (node != null) heap.offer(node);
    ListNode dummy = new ListNode(0), tail = dummy;
    while (!heap.isEmpty()) {
      ListNode node = heap.poll();
      tail.next = node;
      tail = node;
      if (node.next != null) heap.offer(node.next);
    }
    tail.next = null;
    return dummy.next;
  }
}
