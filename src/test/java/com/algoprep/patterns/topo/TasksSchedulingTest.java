package com.algoprep.patterns.topo;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class TasksSchedulingTest { @Test void acceptsAcyclicPrerequisites() { assertTrue(TasksScheduling.isSchedulingPossible(3, new int[][] {{0,1},{1,2}})); } @Test void rejectsCycle() { assertFalse(TasksScheduling.isSchedulingPossible(3, new int[][] {{0,1},{1,2},{2,0}})); } }
