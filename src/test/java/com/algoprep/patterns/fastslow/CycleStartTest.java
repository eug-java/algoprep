package com.algoprep.patterns.fastslow;
import static org.junit.jupiter.api.Assertions.*;
import com.algoprep.common.ListNode;
import org.junit.jupiter.api.Test;
class CycleStartTest {
  @Test void findsCycleEntryAndRejectsAcyclicList() { ListNode head = ListNode.of(1,2,3,4); head.next.next.next.next = head.next; assertSame(head.next, CycleStart.findCycleStart(head)); assertNull(CycleStart.findCycleStart(ListNode.of(1,2))); }
}
