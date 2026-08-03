package com.algoprep.common;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/** Binary tree node used across tree DFS/BFS patterns. */
public class TreeNode {
  public int val;
  public TreeNode left;
  public TreeNode right;

  public TreeNode() {}

  public TreeNode(int val) {
    this.val = val;
  }

  public TreeNode(int val, TreeNode left, TreeNode right) {
    this.val = val;
    this.left = left;
    this.right = right;
  }

  /** Level-order build; nulls allowed as Integer nulls. */
  public static TreeNode of(Integer... values) {
    if (values == null || values.length == 0 || values[0] == null) {
      return null;
    }
    TreeNode root = new TreeNode(values[0]);
    Queue<TreeNode> q = new ArrayDeque<>();
    q.add(root);
    int i = 1;
    while (i < values.length) {
      TreeNode node = q.poll();
      if (node == null) {
        break;
      }
      if (i < values.length && values[i] != null) {
        node.left = new TreeNode(values[i]);
        q.add(node.left);
      }
      i++;
      if (i < values.length && values[i] != null) {
        node.right = new TreeNode(values[i]);
        q.add(node.right);
      }
      i++;
    }
    return root;
  }

  public static List<Integer> levelOrderValues(TreeNode root) {
    List<Integer> out = new ArrayList<>();
    if (root == null) {
      return out;
    }
    Queue<TreeNode> q = new ArrayDeque<>();
    q.add(root);
    while (!q.isEmpty()) {
      TreeNode n = q.poll();
      out.add(n.val);
      if (n.left != null) {
        q.add(n.left);
      }
      if (n.right != null) {
        q.add(n.right);
      }
    }
    return out;
  }
}
