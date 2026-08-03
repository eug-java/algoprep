package com.algoprep.patterns.kwaymerge;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import java.util.List;
import org.junit.jupiter.api.Test;
class SmallestRangeTest {
  @Test void findsSmallestCoveringRange() { assertArrayEquals(new int[] {20,24}, SmallestRange.smallestRange(List.of(List.of(4,10,15,24,26),List.of(0,9,12,20),List.of(5,18,22,30)))); }
}
