package com.algoprep.patterns.treedfs;

import com.algoprep.common.TreeNode;

/** Computes the maximum sum of any non-empty path in a binary tree. */
public final class MaxPathSum {
  private MaxPathSum() {}
  public static int maxPathSum(TreeNode root) {
    if (root == null) return 0;
    int[] best = {Integer.MIN_VALUE}; gain(root, best); return best[0];
  }
  private static int gain(TreeNode node, int[] best) {
    if (node == null) return 0;
    int left = Math.max(0, gain(node.left, best)), right = Math.max(0, gain(node.right, best));
    best[0] = Math.max(best[0], node.val + left + right);
    return node.val + Math.max(left, right);
  }
}
