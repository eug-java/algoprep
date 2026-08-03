package com.algoprep.patterns.tracking;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class MaxPointsOnLineTest { @Test void findsDiagonalLine(){assertEquals(3,MaxPointsOnLine.maxPoints(new int[][]{{1,1},{2,2},{3,3},{3,4}}));} @Test void handlesDuplicatesAndVerticalLine(){assertEquals(4,MaxPointsOnLine.maxPoints(new int[][]{{1,1},{1,1},{1,2},{1,3}}));} }
