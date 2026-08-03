package com.algoprep.patterns.math;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class SqrtXTest { @Test void floorsNonPerfectSquares(){assertEquals(2,SqrtX.mySqrt(8));} @Test void avoidsOverflow(){assertEquals(46340,SqrtX.mySqrt(Integer.MAX_VALUE));} }
