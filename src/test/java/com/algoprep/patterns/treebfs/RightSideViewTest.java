package com.algoprep.patterns.treebfs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.algoprep.common.TreeNode;
import java.util.List;
import org.junit.jupiter.api.Test;

class RightSideViewTest {
  @Test
  void returnsLastNodeAtEachLevel() {
    assertEquals(
        List.of(1, 3, 4), RightSideView.treeRightView(TreeNode.of(1, 2, 3, null, 5, null, 4)));
  }
}
