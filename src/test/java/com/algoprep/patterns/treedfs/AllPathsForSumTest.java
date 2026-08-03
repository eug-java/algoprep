package com.algoprep.patterns.treedfs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.algoprep.common.TreeNode;
import java.util.List;
import org.junit.jupiter.api.Test;

class AllPathsForSumTest {
  @Test
  void findsAllMatchingPaths() {
    TreeNode root = TreeNode.of(12, 7, 1, 4, null, 10, 5);
    assertEquals(
        List.of(List.of(12, 7, 4), List.of(12, 1, 10)), AllPathsForSum.findPaths(root, 23));
  }
}
