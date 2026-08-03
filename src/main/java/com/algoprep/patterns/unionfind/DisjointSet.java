package com.algoprep.patterns.unionfind;

/** Union-find structure with path compression and union by rank. */
public final class DisjointSet {
  private final int[] parent;
  private final int[] rank;

  public DisjointSet(int size) {
    parent = new int[size];
    rank = new int[size];
    for (int i = 0; i < size; i++) parent[i] = i;
  }

  public int find(int value) {
    if (parent[value] != value) parent[value] = find(parent[value]);
    return parent[value];
  }

  public boolean union(int a, int b) {
    int rootA = find(a), rootB = find(b);
    if (rootA == rootB) return false;
    if (rank[rootA] < rank[rootB]) parent[rootA] = rootB;
    else {
      parent[rootB] = rootA;
      if (rank[rootA] == rank[rootB]) rank[rootA]++;
    }
    return true;
  }
}
