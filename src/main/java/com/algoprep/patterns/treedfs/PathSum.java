package com.algoprep.patterns.treedfs;

import com.algoprep.common.TreeNode;

/** Determines whether a root-to-leaf path has a target sum. */
public final class PathSum {
  private PathSum() {}

  public static boolean hasPath(TreeNode root, int sum) {
    if (root == null) return false;
    if (root.left == null && root.right == null) return root.val == sum;
    return hasPath(root.left, sum - root.val) || hasPath(root.right, sum - root.val);
  }
}
