package com.algoprep.patterns.treedfs;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.algoprep.common.TreeNode;
import org.junit.jupiter.api.Test;

class TreeDiameterTest {
  @Test
  void measuresEdgesOnLongestPath() {
    assertEquals(4, TreeDiameter.findDiameter(TreeNode.of(1, 2, 3, 4, 5, null, null, 6)));
  }

  @Test
  void emptyTreeHasZeroDiameter() {
    assertEquals(0, TreeDiameter.findDiameter(null));
  }
}
