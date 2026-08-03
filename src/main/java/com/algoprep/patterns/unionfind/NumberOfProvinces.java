package com.algoprep.patterns.unionfind;

/** Counts connected components in a city adjacency matrix. */
public final class NumberOfProvinces {
  private NumberOfProvinces() {}

  public static int findCircleNum(int[][] isConnected) {
    DisjointSet sets = new DisjointSet(isConnected.length);
    int provinces = isConnected.length;
    for (int i = 0; i < isConnected.length; i++)
      for (int j = i + 1; j < isConnected.length; j++)
        if (isConnected[i][j] == 1 && sets.union(i, j)) provinces--;
    return provinces;
  }
}
