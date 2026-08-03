package com.algoprep.patterns.twopointers;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.Test;
class QuadrupleSumTest {
  @Test void returnsUniqueQuadruplets() { assertEquals(List.of(List.of(-3,-1,1,4), List.of(-3,1,1,2)), QuadrupleSum.searchQuadruplets(new int[] {4,1,2,-1,1,-3}, 1)); }
}
