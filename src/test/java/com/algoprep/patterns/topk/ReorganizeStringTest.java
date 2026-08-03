package com.algoprep.patterns.topk;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
class ReorganizeStringTest {
  @Test void createsValidReorganizationOrEmpty() { String result = ReorganizeString.reorganizeString("aab"); assertEquals(new HashMap<>() {{ put('a',2); put('b',1); }}, count(result)); for (int i=1;i<result.length();i++) assertNotEquals(result.charAt(i-1),result.charAt(i)); assertEquals("", ReorganizeString.reorganizeString("aaab")); }
  private HashMap<Character,Integer> count(String text) { HashMap<Character,Integer> counts=new HashMap<>(); for(char c:text.toCharArray()) counts.merge(c,1,Integer::sum); return counts; }
}
