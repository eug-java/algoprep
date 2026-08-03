package com.algoprep.patterns.graphs;

import java.util.ArrayList;
import java.util.List;

/** Finds all bridges in an undirected network with Tarjan's algorithm. */
public final class CriticalConnections {
  private CriticalConnections() {}
  public static List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) { List<List<Integer>> graph = new ArrayList<>(); for (int i = 0; i < n; i++) graph.add(new ArrayList<>()); for (List<Integer> edge : connections) { int a = edge.get(0), b = edge.get(1); graph.get(a).add(b); graph.get(b).add(a); } int[] discovery = new int[n], low = new int[n]; List<List<Integer>> bridges = new ArrayList<>(); int[] time = {1}; for (int i = 0; i < n; i++) if (discovery[i] == 0) dfs(i, -1, graph, discovery, low, time, bridges); return bridges; }
  private static void dfs(int node, int parent, List<List<Integer>> graph, int[] discovery, int[] low, int[] time, List<List<Integer>> bridges) { discovery[node] = low[node] = time[0]++; for (int next : graph.get(node)) { if (next == parent) continue; if (discovery[next] == 0) { dfs(next, node, graph, discovery, low, time, bridges); low[node] = Math.min(low[node], low[next]); if (low[next] > discovery[node]) bridges.add(List.of(node, next)); } else low[node] = Math.min(low[node], discovery[next]); } }
}
