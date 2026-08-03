package com.algoprep.patterns.treebfs;

import com.algoprep.common.TreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Produces LeetCode 987 vertical traversal ordering. */
public final class VerticalOrder {
  private VerticalOrder() {}
  public static List<List<Integer>> verticalTraversal(TreeNode root) {
    Map<Integer, Map<Integer, List<Integer>>> columns = new TreeMap<>();
    visit(root, 0, 0, columns);
    List<List<Integer>> result = new ArrayList<>();
    for (Map<Integer, List<Integer>> rows : columns.values()) {
      List<Integer> values = new ArrayList<>();
      for (List<Integer> row : rows.values()) { row.sort(Integer::compareTo); values.addAll(row); }
      result.add(values);
    }
    return result;
  }
  private static void visit(TreeNode node, int col, int row, Map<Integer, Map<Integer, List<Integer>>> out) {
    if (node == null) return;
    out.computeIfAbsent(col, ignored -> new TreeMap<>()).computeIfAbsent(row, ignored -> new ArrayList<>()).add(node.val);
    visit(node.left, col - 1, row + 1, out); visit(node.right, col + 1, row + 1, out);
  }
}
