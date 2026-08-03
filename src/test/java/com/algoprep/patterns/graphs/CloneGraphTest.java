package com.algoprep.patterns.graphs;

import static org.junit.jupiter.api.Assertions.*;

import com.algoprep.common.GraphNode;
import org.junit.jupiter.api.Test;

class CloneGraphTest {
  @Test
  void deepCopiesCycle() {
    GraphNode one = new GraphNode(1), two = new GraphNode(2);
    one.neighbors.add(two);
    two.neighbors.add(one);
    GraphNode clone = CloneGraph.cloneGraph(one);
    assertAll(
        () -> assertNotSame(one, clone),
        () -> assertEquals(1, clone.val),
        () -> assertNotSame(two, clone.neighbors.getFirst()),
        () -> assertSame(clone, clone.neighbors.getFirst().neighbors.getFirst()));
  }

  @Test
  void handlesNull() {
    assertNull(CloneGraph.cloneGraph(null));
  }
}
