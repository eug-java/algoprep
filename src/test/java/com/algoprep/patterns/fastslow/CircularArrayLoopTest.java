package com.algoprep.patterns.fastslow;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
class CircularArrayLoopTest {
  @Test void detectsOnlyDirectionalMultiNodeLoops() { assertTrue(CircularArrayLoop.circularArrayLoop(new int[] {1,2,-1,2,2})); assertFalse(CircularArrayLoop.circularArrayLoop(new int[] {2,1,-1,-2})); }
}
