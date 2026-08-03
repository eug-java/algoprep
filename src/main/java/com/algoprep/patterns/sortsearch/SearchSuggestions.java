package com.algoprep.patterns.sortsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Returns up to three lexicographically first matching products per prefix. */
public final class SearchSuggestions {
  private SearchSuggestions() {}

  public static List<List<String>> suggestedProducts(String[] products, String searchWord) {
    Arrays.sort(products);
    List<List<String>> result = new ArrayList<>();
    String prefix = "";
    for (char letter : searchWord.toCharArray()) {
      prefix += letter;
      List<String> suggestions = new ArrayList<>(3);
      for (String product : products) {
        if (product.startsWith(prefix)) {
          suggestions.add(product);
          if (suggestions.size() == 3) break;
        }
      }
      result.add(suggestions);
    }
    return result;
  }
}
