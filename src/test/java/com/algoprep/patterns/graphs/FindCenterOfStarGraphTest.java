package com.algoprep.patterns.graphs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class FindCenterOfStarGraphTest {
  @Test void findsSharedEndpointRegardlessOfEdgeOrder() {
    assertEquals(2, FindCenterOfStarGraph.findCenter(new int[][] {{1, 2}, {2, 3}, {4, 2}}));
    assertEquals(1, FindCenterOfStarGraph.findCenter(new int[][] {{1, 2}, {3, 1}}));
  }

  @Test void requiresAtLeastTwoEdges() {
    assertThrows(IllegalArgumentException.class, () -> FindCenterOfStarGraph.findCenter(new int[][] {{1, 2}}));
  }
}
