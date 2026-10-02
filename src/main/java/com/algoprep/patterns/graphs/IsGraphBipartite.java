package com.algoprep.patterns.graphs;

import java.util.ArrayDeque;

/** True when the undirected graph can be colored with two colors and no edge joins equal colors. */
public final class IsGraphBipartite {
  private IsGraphBipartite() {}

  public static boolean isBipartite(int[][] graph) {
    if (graph == null) return true;
    int[] color = new int[graph.length];
    for (int start = 0; start < graph.length; start++) {
      if (color[start] != 0) continue;
      ArrayDeque<Integer> queue = new ArrayDeque<>();
      queue.add(start);
      color[start] = 1;
      while (!queue.isEmpty()) {
        int node = queue.remove();
        for (int neighbor : graph[node]) {
          if (color[neighbor] == 0) {
            color[neighbor] = -color[node];
            queue.add(neighbor);
          } else if (color[neighbor] == color[node]) {
            return false;
          }
        }
      }
    }
    return true;
  }
}
