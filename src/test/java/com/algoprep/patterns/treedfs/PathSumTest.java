package com.algoprep.patterns.treedfs;

import static org.junit.jupiter.api.Assertions.*;

import com.algoprep.common.TreeNode;
import org.junit.jupiter.api.Test;

class PathSumTest {
  @Test
  void findsRootToLeafSum() {
    TreeNode root = TreeNode.of(5, 4, 8, 11, null, 13, 4, 7, 2);
    assertTrue(PathSum.hasPath(root, 22));
    assertFalse(PathSum.hasPath(root, 19));
  }

  @Test
  void handlesEmptyTree() {
    assertFalse(PathSum.hasPath(null, 0));
  }
}
