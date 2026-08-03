package com.algoprep.patterns.treedfs;

import com.algoprep.common.TreeNode;

/** Finds the lowest shared ancestor of two nodes in a binary tree. */
public final class LowestCommonAncestor {
  private LowestCommonAncestor() {}
  public static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
    if (root == null || root == p || root == q) return root;
    TreeNode left = lowestCommonAncestor(root.left, p, q);
    TreeNode right = lowestCommonAncestor(root.right, p, q);
    return left != null && right != null ? root : (left != null ? left : right);
  }
}
