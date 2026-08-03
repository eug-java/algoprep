package com.algoprep.patterns.topk;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
class KthLargestTest {
  @Test void findsKthLargestAndValidatesK() { assertEquals(5, KthLargest.findKthLargest(new int[] {3,2,1,5,6,4},2)); assertThrows(IllegalArgumentException.class, () -> KthLargest.findKthLargest(new int[] {1},0)); }
}
