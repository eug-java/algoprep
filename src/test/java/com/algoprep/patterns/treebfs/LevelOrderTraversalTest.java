package com.algoprep.patterns.treebfs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.algoprep.common.TreeNode;
import java.util.List;
import org.junit.jupiter.api.Test;

class LevelOrderTraversalTest {
  @Test
  void traversesLevels() {
    assertEquals(
        List.of(List.of(1), List.of(2, 3), List.of(4, 5)),
        LevelOrderTraversal.traverse(TreeNode.of(1, 2, 3, 4, 5)));
  }

  @Test
  void handlesEmptyTree() {
    assertEquals(List.of(), LevelOrderTraversal.traverse(null));
  }
}
