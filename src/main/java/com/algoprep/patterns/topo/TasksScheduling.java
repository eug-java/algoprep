package com.algoprep.patterns.topo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/** Determines whether prerequisite tasks form an acyclic schedule. */
public final class TasksScheduling {
  private TasksScheduling() {}
  public static boolean isSchedulingPossible(int tasks, int[][] prerequisites) {
    if (tasks < 0) return false; List<List<Integer>> graph = new ArrayList<>(); int[] degree = new int[tasks]; for (int i = 0; i < tasks; i++) graph.add(new ArrayList<>());
    for (int[] edge : prerequisites) { if (edge == null || edge.length != 2 || edge[0] < 0 || edge[0] >= tasks || edge[1] < 0 || edge[1] >= tasks) return false; graph.get(edge[0]).add(edge[1]); degree[edge[1]]++; }
    ArrayDeque<Integer> queue = new ArrayDeque<>(); for (int i = 0; i < tasks; i++) if (degree[i] == 0) queue.add(i); int completed = 0;
    while (!queue.isEmpty()) { int task = queue.remove(); completed++; for (int next : graph.get(task)) if (--degree[next] == 0) queue.add(next); } return completed == tasks;
  }
}
