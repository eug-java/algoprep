package com.algoprep.patterns.customds;
import static org.junit.jupiter.api.Assertions.*; import org.junit.jupiter.api.Test;
class TimeMapTest { @Test void getsLatestPriorValue(){TimeMap map=new TimeMap();map.set("foo","bar",1);map.set("foo","bar2",4);assertEquals("bar",map.get("foo",3));assertEquals("bar2",map.get("foo",4));} @Test void returnsEmptyBeforeFirstValue(){TimeMap map=new TimeMap();map.set("x","v",5);assertEquals("",map.get("x",4));assertEquals("",map.get("missing",9));} }
