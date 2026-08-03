package com.algoprep.patterns.binarysearch;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
class FindPeakElementTest {
  @Test void findsAPeakOrMissingValue() { assertEquals(2, FindPeakElement.findPeakElement(new int[] {1,2,3,1})); assertEquals(-1, FindPeakElement.findPeakElement(new int[0])); }
}
