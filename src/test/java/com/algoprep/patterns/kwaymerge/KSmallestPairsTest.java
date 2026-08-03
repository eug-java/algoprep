package com.algoprep.patterns.kwaymerge;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import org.junit.jupiter.api.Test;
class KSmallestPairsTest {
  @Test void returnsPairsInIncreasingSumOrder() { assertEquals(List.of(List.of(1,2),List.of(1,4),List.of(1,6)), KSmallestPairs.kSmallestPairs(new int[] {1,7,11},new int[] {2,4,6},3)); }
}
