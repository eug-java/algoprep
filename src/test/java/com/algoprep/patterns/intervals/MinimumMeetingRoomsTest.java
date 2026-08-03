package com.algoprep.patterns.intervals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.algoprep.common.Interval;
import org.junit.jupiter.api.Test;
class MinimumMeetingRoomsTest {
  @Test void countsConcurrentMeetings() { assertEquals(2, MinimumMeetingRooms.minMeetingRooms(new Interval[] {new Interval(1,4),new Interval(2,5),new Interval(7,9)})); assertEquals(0, MinimumMeetingRooms.minMeetingRooms(new Interval[0])); }
}
