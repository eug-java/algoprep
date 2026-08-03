package com.algoprep.patterns.twopointers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
class ContainerWithMostWaterTest {
  @Test void findsOptimalContainer() { assertEquals(49, ContainerWithMostWater.maxArea(new int[] {1,8,6,2,5,4,8,3,7})); assertEquals(0, ContainerWithMostWater.maxArea(new int[] {4})); }
}
