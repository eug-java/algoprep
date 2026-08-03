package com.algoprep.patterns.bitwise;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class SumOfTwoIntegersTest { @Test void addsSigns(){assertEquals(3,SumOfTwoIntegers.getSum(1,2));assertEquals(1,SumOfTwoIntegers.getSum(-2,3));} @Test void wrapsLikeIntegerAddition(){assertEquals(Integer.MIN_VALUE,SumOfTwoIntegers.getSum(Integer.MAX_VALUE,1));} }
