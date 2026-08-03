package com.algoprep.patterns.binarysearch;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class OrderAgnosticBinarySearchTest {
  @Test
  void searchesBothOrders() {
    assertEquals(2, OrderAgnosticBinarySearch.search(new int[] {4, 6, 10}, 10));
    assertEquals(2, OrderAgnosticBinarySearch.search(new int[] {10, 6, 4}, 4));
    assertEquals(-1, OrderAgnosticBinarySearch.search(new int[] {}, 1));
  }
}
