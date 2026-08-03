package com.algoprep.patterns.math;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class IntegerToRomanTest { @Test void usesSubtractiveNotation(){assertEquals("MCMXCIV",IntegerToRoman.intToRoman(1994));} @Test void convertsSmallNumber(){assertEquals("LVIII",IntegerToRoman.intToRoman(58));} }
