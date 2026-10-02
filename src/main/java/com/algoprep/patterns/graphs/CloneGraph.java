package com.algoprep.patterns.graphs;

import com.algoprep.common.GraphNode;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/** Produces a deep copy of a connected graph. */
public final class CloneGraph {
  private CloneGraph() {}

  public static GraphNode cloneGraph(GraphNode node) {
    return copy(node, new HashMap<>());
  }

  private static GraphNode copy(GraphNode node, Map<GraphNode, GraphNode> copies) {
    if (node == null) return null;
    if (copies.containsKey(node)) return copies.get(node);
    GraphNode clone = new GraphNode(node.val);
    copies.put(node, clone);
    for (GraphNode neighbor : node.neighbors) clone.neighbors.add(copy(neighbor, copies));
    return clone;
  }

  /** Playground form: adjacency lists are 1-indexed, and each neighbor row is returned sorted. */
  public static int[][] cloneGraph(int[][] adj) {
    if (adj == null || adj.length == 0) return new int[0][];
    return toAdj(cloneGraph(fromAdj(adj)), adj.length);
  }

  private static GraphNode fromAdj(int[][] adj) {
    GraphNode[] nodes = new GraphNode[adj.length];
    for (int i = 0; i < adj.length; i++) nodes[i] = new GraphNode(i + 1);
    for (int i = 0; i < adj.length; i++) {
      for (int value : adj[i]) nodes[i].neighbors.add(nodes[value - 1]);
    }
    return nodes[0];
  }

  private static int[][] toAdj(GraphNode root, int n) {
    GraphNode[] byValue = new GraphNode[n + 1];
    ArrayDeque<GraphNode> queue = new ArrayDeque<>();
    queue.add(root);
    byValue[root.val] = root;
    while (!queue.isEmpty()) {
      GraphNode node = queue.remove();
      for (GraphNode neighbor : node.neighbors) {
        if (byValue[neighbor.val] == null) {
          byValue[neighbor.val] = neighbor;
          queue.add(neighbor);
        }
      }
    }
    int[][] out = new int[n][];
    for (int value = 1; value <= n; value++) {
      int[] row = new int[byValue[value].neighbors.size()];
      for (int i = 0; i < row.length; i++) row[i] = byValue[value].neighbors.get(i).val;
      Arrays.sort(row);
      out[value - 1] = row;
    }
    return out;
  }
}
