package com.algoprep.patterns.cyclicsort;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class FindDuplicateNumberTest { @Test void findsDuplicateWithoutMutation() { int[] input = {3,1,3,4,2}; assertEquals(3, FindDuplicateNumber.findDuplicate(input)); assertArrayEquals(new int[] {3,1,3,4,2}, input); } @Test void findsRepeatedValue() { assertEquals(2, FindDuplicateNumber.findDuplicate(new int[] {1,3,4,2,2})); } }
