package com.algoprep.patterns.graphs;

/** Finds the shared endpoint of the first two edges in a valid star graph. */
public final class FindCenterOfStarGraph {
  private FindCenterOfStarGraph() {}

  public static int findCenter(int[][] edges) {
    if (edges == null || edges.length < 2 || edges[0].length != 2 || edges[1].length != 2) {
      throw new IllegalArgumentException("at least two two-node edges are required");
    }
    int candidate = edges[0][0];
    return candidate == edges[1][0] || candidate == edges[1][1] ? candidate : edges[0][1];
  }
}
