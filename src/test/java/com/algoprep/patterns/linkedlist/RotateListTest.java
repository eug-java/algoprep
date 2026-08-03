package com.algoprep.patterns.linkedlist;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.algoprep.common.ListNode;
import java.util.List;
import org.junit.jupiter.api.Test;
class RotateListTest {
  @Test void rotatesAndNormalizesLargeK() { assertEquals(List.of(4,5,1,2,3), ListNode.toList(RotateList.rotateRight(ListNode.of(1,2,3,4,5),2))); assertEquals(List.of(1,2), ListNode.toList(RotateList.rotateRight(ListNode.of(1,2),4))); }
}
