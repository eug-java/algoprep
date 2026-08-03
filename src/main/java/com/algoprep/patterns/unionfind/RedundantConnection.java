package com.algoprep.patterns.unionfind;

/** Identifies the edge that introduces a cycle in a graph. */
public final class RedundantConnection {
  private RedundantConnection() {}

  public static int[] findRedundantConnection(int[][] edges) {
    int max = 0;
    for (int[] edge : edges) max = Math.max(max, Math.max(edge[0], edge[1]));
    DisjointSet sets = new DisjointSet(max + 1);
    for (int[] edge : edges) if (!sets.union(edge[0], edge[1])) return edge;
    return new int[0];
  }
}
