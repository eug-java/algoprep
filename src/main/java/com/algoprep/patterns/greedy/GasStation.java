package com.algoprep.patterns.greedy;

/** Finds a viable starting gas station, if one exists. */
public final class GasStation {
  private GasStation() {}
  public static int canCompleteCircuit(int[] gas, int[] cost) {
    if (gas == null || cost == null || gas.length != cost.length || gas.length == 0) return -1;
    int total = 0, tank = 0, start = 0;
    for (int i = 0; i < gas.length; i++) { int gain = gas[i] - cost[i]; total += gain; tank += gain; if (tank < 0) { start = i + 1; tank = 0; } }
    return total < 0 ? -1 : start;
  }
}
