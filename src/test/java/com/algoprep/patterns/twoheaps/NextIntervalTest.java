package com.algoprep.patterns.twoheaps;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import com.algoprep.common.Interval;
import org.junit.jupiter.api.Test;
class NextIntervalTest {
  @Test void findsEarliestValidNextInterval() { assertArrayEquals(new int[] {1,2,-1}, NextInterval.findNextInterval(new Interval[] {new Interval(2,3),new Interval(3,4),new Interval(5,6)})); }
}
