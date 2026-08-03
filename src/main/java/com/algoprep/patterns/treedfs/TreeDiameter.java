package com.algoprep.patterns.treedfs;

import com.algoprep.common.TreeNode;

/** Calculates the maximum edge distance between two tree nodes. */
public final class TreeDiameter {
  private TreeDiameter() {}

  public static int findDiameter(TreeNode root) {
    int[] diameter = new int[1];
    depth(root, diameter);
    return diameter[0];
  }

  private static int depth(TreeNode node, int[] diameter) {
    if (node == null) return 0;
    int left = depth(node.left, diameter), right = depth(node.right, diameter);
    diameter[0] = Math.max(diameter[0], left + right);
    return 1 + Math.max(left, right);
  }
}
