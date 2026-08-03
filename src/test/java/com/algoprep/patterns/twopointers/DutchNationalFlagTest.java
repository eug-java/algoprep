package com.algoprep.patterns.twopointers;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;
class DutchNationalFlagTest {
  @Test void sortsMixedColorsInPlace() { int[] values = {2,0,2,1,1,0}; DutchNationalFlag.sortColors(values); assertArrayEquals(new int[] {0,0,1,1,2,2}, values); }
}
