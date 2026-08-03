package com.algoprep.patterns.greedy;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class GasStationTest { @Test void findsViableStart() { assertEquals(3, GasStation.canCompleteCircuit(new int[] {1,2,3,4,5}, new int[] {3,4,5,1,2})); } @Test void reportsImpossibleCircuit() { assertEquals(-1, GasStation.canCompleteCircuit(new int[] {2,3,4}, new int[] {3,4,3})); } }
