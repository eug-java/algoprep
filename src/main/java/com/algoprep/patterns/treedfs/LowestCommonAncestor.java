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

  /** Playground form: the judge passes node values because it cannot share object identity. */
  public static int lowestCommonAncestor(TreeNode root, int p, int q) {
    TreeNode ancestor = lowestCommonAncestor(root, find(root, p), find(root, q));
    if (ancestor == null) throw new IllegalArgumentException("nodes are not in the tree");
    return ancestor.val;
  }

  private static TreeNode find(TreeNode node, int value) {
    if (node == null) return null;
    if (node.val == value) return node;
    TreeNode left = find(node.left, value);
    return left != null ? left : find(node.right, value);
  }
}
