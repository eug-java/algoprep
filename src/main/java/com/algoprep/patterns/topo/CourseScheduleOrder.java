package com.algoprep.patterns.topo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/** Produces a valid prerequisite-respecting course order when one exists. */
public final class CourseScheduleOrder {
  private CourseScheduleOrder() {}

  public static int[] findOrder(int numCourses, int[][] prerequisites) {
    List<List<Integer>> graph = new ArrayList<>();
    for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
    int[] indegree = new int[numCourses];
    for (int[] pair : prerequisites) {
      graph.get(pair[1]).add(pair[0]);
      indegree[pair[0]]++;
    }
    Queue<Integer> sources = new ArrayDeque<>();
    for (int i = 0; i < numCourses; i++) if (indegree[i] == 0) sources.add(i);
    int[] order = new int[numCourses];
    int count = 0;
    while (!sources.isEmpty()) {
      int course = sources.remove();
      order[count++] = course;
      for (int next : graph.get(course)) if (--indegree[next] == 0) sources.add(next);
    }
    return count == numCourses ? order : new int[0];
  }
}
