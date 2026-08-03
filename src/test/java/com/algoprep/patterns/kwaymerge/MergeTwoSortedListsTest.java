package com.algoprep.patterns.kwaymerge;

import static org.junit.jupiter.api.Assertions.*;

import com.algoprep.common.ListNode;
import java.util.List;
import org.junit.jupiter.api.Test;

class MergeTwoSortedListsTest {
  @Test void mergesInterleavedListsIncludingDuplicates() {
    assertEquals(
        List.of(1, 1, 2, 3, 4, 4),
        ListNode.toList(MergeTwoSortedLists.mergeTwoLists(ListNode.of(1, 2, 4), ListNode.of(1, 3, 4))));
  }

  @Test void handlesEmptyLists() {
    assertEquals(List.of(1, 2), ListNode.toList(MergeTwoSortedLists.mergeTwoLists(null, ListNode.of(1, 2))));
    assertNull(MergeTwoSortedLists.mergeTwoLists(null, null));
  }
}
