package com.algoprep.patterns.sortsearch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

class SearchSuggestionsTest {
  @Test
  void suggestsThreeLexicographicMatchesPerPrefix() {
    assertEquals(
        List.of(
            List.of("mobile", "moneypot", "monitor"),
            List.of("mobile", "moneypot", "monitor"),
            List.of("mouse", "mousepad"),
            List.of("mouse", "mousepad"),
            List.of("mouse", "mousepad")),
        SearchSuggestions.suggestedProducts(
            new String[] {"mobile", "mouse", "moneypot", "monitor", "mousepad"}, "mouse"));
  }

  @Test
  void returnsEmptySuggestionsAfterNoMatch() {
    assertEquals(
        List.of(List.of("bags"), List.of(), List.of()),
        SearchSuggestions.suggestedProducts(new String[] {"bags"}, "box"));
  }
}
