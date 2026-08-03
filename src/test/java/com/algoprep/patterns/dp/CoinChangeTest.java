package com.algoprep.patterns.dp;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class CoinChangeTest { @Test void findsMinimumCoins() { assertEquals(3, CoinChange.coinChange(new int[] {1,2,5}, 11)); } @Test void returnsMinusOneWhenImpossible() { assertEquals(-1, CoinChange.coinChange(new int[] {2}, 3)); } }
