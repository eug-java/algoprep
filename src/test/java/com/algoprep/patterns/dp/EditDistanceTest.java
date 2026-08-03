package com.algoprep.patterns.dp;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class EditDistanceTest { @Test void calculatesEdits() { assertEquals(3, EditDistance.minDistance("horse", "ros")); } @Test void handlesEmptyWord() { assertEquals(3, EditDistance.minDistance("", "abc")); } }
