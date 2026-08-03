package com.algoprep.patterns.linkedlist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.algoprep.common.ListNode;
import java.util.List;
import org.junit.jupiter.api.Test;
class ReorderListTest {
  @Test void interleavesEndsOfEvenAndOddLists() { ListNode even = ListNode.of(1,2,3,4); ReorderList.reorderList(even); assertEquals(List.of(1,4,2,3), ListNode.toList(even)); ListNode odd = ListNode.of(1,2,3,4,5); ReorderList.reorderList(odd); assertEquals(List.of(1,5,2,4,3), ListNode.toList(odd)); }
}
