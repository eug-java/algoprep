package com.algoprep.patterns.treebfs;

import com.algoprep.common.TreeNode;
import java.util.*;

/** Traverses a binary tree one level at a time. */
public final class LevelOrderTraversal {
  private LevelOrderTraversal() {}

  public static List<List<Integer>> traverse(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new ArrayDeque<>();
    queue.add(root);
    while (!queue.isEmpty()) {
      int size = queue.size();
      List<Integer> level = new ArrayList<>(size);
      while (size-- > 0) {
        TreeNode n = queue.remove();
        level.add(n.val);
        if (n.left != null) queue.add(n.left);
        if (n.right != null) queue.add(n.right);
      }
      result.add(level);
    }
    return result;
  }
}
