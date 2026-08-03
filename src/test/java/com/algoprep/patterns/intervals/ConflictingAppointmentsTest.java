package com.algoprep.patterns.intervals;

import static org.junit.jupiter.api.Assertions.*;

import com.algoprep.common.Interval;
import org.junit.jupiter.api.Test;

class ConflictingAppointmentsTest {
  @Test
  void identifiesOverlap() {
    assertFalse(
        ConflictingAppointments.canAttendAllAppointments(
            new Interval[] {new Interval(4, 5), new Interval(1, 4), new Interval(3, 6)}));
    assertTrue(
        ConflictingAppointments.canAttendAllAppointments(
            new Interval[] {new Interval(1, 3), new Interval(3, 5)}));
  }

  @Test
  void handlesEmpty() {
    assertTrue(ConflictingAppointments.canAttendAllAppointments(new Interval[] {}));
  }
}
