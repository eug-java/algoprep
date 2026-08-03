package com.algoprep.patterns.treebfs;

import com.algoprep.common.TreeNode;
import java.util.ArrayDeque;
import java.util.Queue;

/** Finds the shortest root-to-leaf path length. */
public final class MinDepth {
  private MinDepth() {}
  public static int minDepth(TreeNode root) {
    if (root == null) return 0;
    Queue<TreeNode> queue = new ArrayDeque<>(); queue.add(root); int depth = 1;
    while (!queue.isEmpty()) {
      for (int size = queue.size(); size > 0; size--) {
        TreeNode node = queue.remove();
        if (node.left == null && node.right == null) return depth;
        if (node.left != null) queue.add(node.left); if (node.right != null) queue.add(node.right);
      }
      depth++;
    }
    return depth;
  }
}
