package com.algoprep.patterns.fastslow;
import static org.junit.jupiter.api.Assertions.*;
import com.algoprep.common.ListNode;
import java.util.List;
import org.junit.jupiter.api.Test;
class PalindromeLinkedListTest {
  @Test void checksAndRestoresList() { ListNode list = ListNode.of(2,4,6,4,2); assertTrue(PalindromeLinkedList.isPalindrome(list)); assertEquals(List.of(2,4,6,4,2), ListNode.toList(list)); assertFalse(PalindromeLinkedList.isPalindrome(ListNode.of(1,2))); }
}
