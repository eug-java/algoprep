package com.algoprep.patterns.treebfs;

import com.algoprep.common.TreeNode;
import java.util.*;

/** Traverses tree levels while alternating output direction. */
public final class ZigzagLevelOrder {
  private ZigzagLevelOrder() {}

  public static List<List<Integer>> traverse(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    Queue<TreeNode> queue = new ArrayDeque<>();
    queue.add(root);
    boolean leftToRight = true;
    while (!queue.isEmpty()) {
      int size = queue.size();
      LinkedList<Integer> level = new LinkedList<>();
      while (size-- > 0) {
        TreeNode n = queue.remove();
        if (leftToRight) level.addLast(n.val);
        else level.addFirst(n.val);
        if (n.left != null) queue.add(n.left);
        if (n.right != null) queue.add(n.right);
      }
      result.add(level);
      leftToRight = !leftToRight;
    }
    return result;
  }
}
