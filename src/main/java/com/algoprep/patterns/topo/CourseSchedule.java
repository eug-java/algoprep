package com.algoprep.patterns.topo;


/** Determines whether all course prerequisites can be completed. */
public final class CourseSchedule {
  private CourseSchedule() {}

  public static boolean canFinish(int numCourses, int[][] prerequisites) {
    return CourseScheduleOrder.findOrder(numCourses, prerequisites).length == numCourses;
  }
}
