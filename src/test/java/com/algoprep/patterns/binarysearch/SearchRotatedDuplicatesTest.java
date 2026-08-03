package com.algoprep.patterns.binarysearch;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
class SearchRotatedDuplicatesTest {
  @Test void searchesThroughAmbiguousDuplicates() { assertTrue(SearchRotatedDuplicates.search(new int[] {2,5,6,0,0,1,2},0)); assertFalse(SearchRotatedDuplicates.search(new int[] {1,0,1,1,1},3)); }
}
