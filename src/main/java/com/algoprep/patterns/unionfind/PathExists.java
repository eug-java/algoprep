package com.algoprep.patterns.unionfind;

/** Determines whether two graph nodes are connected using disjoint sets. */
public final class PathExists {
  private PathExists() {}

  public static boolean validPath(int n, int[][] edges, int source, int destination) {
    if (n < 0 || edges == null || source < 0 || destination < 0 || source >= n || destination >= n) {
      throw new IllegalArgumentException("invalid graph or vertex");
    }
    DisjointSet sets = new DisjointSet(n);
    for (int[] edge : edges) {
      if (edge == null || edge.length != 2 || edge[0] < 0 || edge[1] < 0 || edge[0] >= n || edge[1] >= n) {
        throw new IllegalArgumentException("invalid edge");
      }
      sets.union(edge[0], edge[1]);
    }
    return sets.find(source) == sets.find(destination);
  }

  private static final class DisjointSet {
    private final int[] parent;
    private final int[] rank;

    private DisjointSet(int size) {
      parent = new int[size];
      rank = new int[size];
      for (int i = 0; i < size; i++) parent[i] = i;
    }

    private int find(int value) {
      if (parent[value] != value) parent[value] = find(parent[value]);
      return parent[value];
    }

    private void union(int first, int second) {
      int firstRoot = find(first);
      int secondRoot = find(second);
      if (firstRoot == secondRoot) return;
      if (rank[firstRoot] < rank[secondRoot]) parent[firstRoot] = secondRoot;
      else if (rank[firstRoot] > rank[secondRoot]) parent[secondRoot] = firstRoot;
      else {
        parent[secondRoot] = firstRoot;
        rank[firstRoot]++;
      }
    }
  }
}
