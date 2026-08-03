package com.algoprep.patterns.tracking;
import static org.junit.jupiter.api.Assertions.*; import java.util.List; import org.junit.jupiter.api.Test;
class MajorityElementIITest { @Test void findsTwoCandidates(){assertEquals(List.of(1,2),MajorityElementII.majorityElement(new int[]{1,2,1,2,1,2,3}));} @Test void returnsEmptyWithoutMajority(){assertTrue(MajorityElementII.majorityElement(new int[]{1,2,3,4}).isEmpty());} }
