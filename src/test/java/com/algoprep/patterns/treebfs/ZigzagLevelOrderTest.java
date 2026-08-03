package com.algoprep.patterns.treebfs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.algoprep.common.TreeNode;
import java.util.List;
import org.junit.jupiter.api.Test;

class ZigzagLevelOrderTest {
  @Test
  void alternatesLevelDirection() {
    assertEquals(
        List.of(List.of(1), List.of(3, 2), List.of(4, 5)),
        ZigzagLevelOrder.traverse(TreeNode.of(1, 2, 3, 4, 5)));
  }
}
