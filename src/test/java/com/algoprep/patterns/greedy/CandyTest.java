package com.algoprep.patterns.greedy;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class CandyTest { @Test void handlesPeaks() { assertEquals(5, Candy.candy(new int[] {1,0,2})); } @Test void handlesEqualRatings() { assertEquals(4, Candy.candy(new int[] {1,2,2})); } }
