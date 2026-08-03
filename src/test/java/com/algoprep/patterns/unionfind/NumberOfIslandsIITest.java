package com.algoprep.patterns.unionfind;
import static org.junit.jupiter.api.Assertions.*; import java.util.List; import org.junit.jupiter.api.Test;
class NumberOfIslandsIITest { @Test void tracksMergingIslands(){assertEquals(List.of(1,1,2,3,1),NumberOfIslandsII.numIslands2(3,3,new int[][]{{0,0},{0,1},{1,2},{2,1},{1,1}}));} @Test void ignoresDuplicateLand(){assertEquals(List.of(1,1),NumberOfIslandsII.numIslands2(1,1,new int[][]{{0,0},{0,0}}));} }
