package com.algoprep.patterns.treebfs;

import com.algoprep.common.TreeNode;
import java.util.*;

/** Returns the node visible from the right at each tree level. */
public final class RightSideView {
  private RightSideView() {}

  public static List<Integer> treeRightView(TreeNode root) {
    List<Integer> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new ArrayDeque<>();
    queue.add(root);
    while (!queue.isEmpty()) {
      int size = queue.size();
      for (int i = 0; i < size; i++) {
        TreeNode n = queue.remove();
        if (i == size - 1) result.add(n.val);
        if (n.left != null) queue.add(n.left);
        if (n.right != null) queue.add(n.right);
      }
    }
    return result;
  }
}
