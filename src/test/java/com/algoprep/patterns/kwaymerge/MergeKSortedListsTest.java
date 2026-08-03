package com.algoprep.patterns.kwaymerge;

import static org.junit.jupiter.api.Assertions.*;

import com.algoprep.common.ListNode;
import java.util.*;
import org.junit.jupiter.api.Test;

class MergeKSortedListsTest {
  @Test
  void mergesListsIncludingEmptyList() {
    assertEquals(
        List.of(1, 2, 3, 4, 5, 6),
        ListNode.toList(
            MergeKSortedLists.merge(
                new ListNode[] {ListNode.of(1, 4, 5), null, ListNode.of(2, 3, 6)})));
  }

  @Test
  void handlesNoLists() {
    assertNull(MergeKSortedLists.merge(new ListNode[] {}));
  }
}
