package com.algoprep.patterns.unionfind;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PathExistsTest {
  @Test void findsPathAcrossMultipleEdges() {
    assertTrue(PathExists.validPath(6, new int[][] {{0, 1}, {1, 2}, {2, 5}, {3, 4}}, 0, 5));
  }

  @Test void rejectsNodesInSeparateComponentsAndAcceptsSameNode() {
    assertFalse(PathExists.validPath(4, new int[][] {{0, 1}, {2, 3}}, 0, 3));
    assertTrue(PathExists.validPath(1, new int[][] {}, 0, 0));
  }
}
