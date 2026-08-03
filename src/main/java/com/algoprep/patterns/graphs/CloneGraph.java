package com.algoprep.patterns.graphs;

import com.algoprep.common.GraphNode;
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
}
