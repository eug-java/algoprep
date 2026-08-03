package com.algoprep.patterns.intervals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.algoprep.common.Interval;
import java.util.List;
import org.junit.jupiter.api.Test;
class IntervalIntersectionTest {
  @Test void intersectsInclusiveIntervals() { assertEquals(List.of(new Interval(2,3),new Interval(5,6),new Interval(7,7)), IntervalIntersection.intervalIntersection(List.of(new Interval(1,3),new Interval(5,7)),List.of(new Interval(2,6),new Interval(7,9)))); }
}
