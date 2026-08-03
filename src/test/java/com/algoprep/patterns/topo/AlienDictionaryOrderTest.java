package com.algoprep.patterns.topo;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class AlienDictionaryOrderTest { @Test void derivesValidOrder() { assertEquals("wertf", AlienDictionaryOrder.alienOrder(new String[] {"wrt","wrf","er","ett","rftt"})); } @Test void rejectsInvalidPrefix() { assertEquals("", AlienDictionaryOrder.alienOrder(new String[] {"abc", "ab"})); } @Test void rejectsCycle() { assertEquals("", AlienDictionaryOrder.alienOrder(new String[] {"z", "x", "z"})); } }
