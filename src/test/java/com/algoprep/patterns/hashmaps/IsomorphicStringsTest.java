package com.algoprep.patterns.hashmaps;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class IsomorphicStringsTest { @Test void acceptsBijectiveMapping(){assertTrue(IsomorphicStrings.isIsomorphic("egg","add"));} @Test void rejectsManyToOneMapping(){assertFalse(IsomorphicStrings.isIsomorphic("foo","bar"));assertFalse(IsomorphicStrings.isIsomorphic("ab","aa"));} }
