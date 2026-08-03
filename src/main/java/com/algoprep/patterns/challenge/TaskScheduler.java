package com.algoprep.patterns.challenge;

import java.util.Arrays;

/**
 * Least intervals to finish all tasks with cooling interval n between equal tasks.
 * Greedy frequency math or heap simulation both work in interviews.
 */
public final class TaskScheduler {
  private TaskScheduler() {}

  public static int leastInterval(char[] tasks, int n) {
    if (tasks == null || tasks.length == 0) return 0;
    int[] freq = new int[26];
    for (char task : tasks) freq[task - 'A']++;
    Arrays.sort(freq);
    int max = freq[25];
    int idleSlots = (max - 1) * n;
    for (int i = 24; i >= 0 && idleSlots > 0; i--) {
      idleSlots -= Math.min(max - 1, freq[i]);
    }
    idleSlots = Math.max(0, idleSlots);
    return tasks.length + idleSlots;
  }
}
