package com.algoprep.patterns.math;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class ValidSquareTest { @Test void acceptsRotatedSquare(){assertTrue(ValidSquare.validSquare(new int[]{0,1},new int[]{1,0},new int[]{0,-1},new int[]{-1,0}));} @Test void rejectsRectangleAndDuplicates(){assertFalse(ValidSquare.validSquare(new int[]{0,0},new int[]{2,0},new int[]{2,1},new int[]{0,1}));assertFalse(ValidSquare.validSquare(new int[]{0,0},new int[]{0,0},new int[]{1,1},new int[]{1,-1}));} }
