package com.algoprep.patterns.treedfs;

import com.algoprep.common.TreeNode;
import java.util.ArrayList;
import java.util.List;

/** Finds every root-to-leaf path with a target sum. */
public final class AllPathsForSum {
  private AllPathsForSum() {}

  public static List<List<Integer>> findPaths(TreeNode root, int sum) {
    List<List<Integer>> paths = new ArrayList<>();
    visit(root, sum, new ArrayList<>(), paths);
    return paths;
  }

  private static void visit(
      TreeNode node, int remaining, List<Integer> path, List<List<Integer>> paths) {
    if (node == null) return;
    path.add(node.val);
    if (node.left == null && node.right == null && node.val == remaining)
      paths.add(new ArrayList<>(path));
    else {
      visit(node.left, remaining - node.val, path, paths);
      visit(node.right, remaining - node.val, path, paths);
    }
    path.remove(path.size() - 1);
  }
}
