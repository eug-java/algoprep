package com.algoprep.patterns.challenge;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/** Finds the shortest one-letter-at-a-time transformation sequence. */
public final class WordLadder {
  private WordLadder() {}

  public static int ladderLength(String beginWord, String endWord, List<String> wordList) {
    if (beginWord == null
        || endWord == null
        || wordList == null
        || beginWord.length() != endWord.length()) {
      return 0;
    }

    Set<String> remaining = new HashSet<>(wordList);
    if (!remaining.remove(endWord)) {
      return 0;
    }

    Queue<String> queue = new ArrayDeque<>();
    queue.add(beginWord);
    int length = 1;

    while (!queue.isEmpty()) {
      for (int levelSize = queue.size(); levelSize > 0; levelSize--) {
        char[] letters = queue.remove().toCharArray();
        for (int i = 0; i < letters.length; i++) {
          char original = letters[i];
          for (char replacement = 'a'; replacement <= 'z'; replacement++) {
            if (replacement == original) {
              continue;
            }
            letters[i] = replacement;
            String candidate = new String(letters);
            if (candidate.equals(endWord)) {
              return length + 1;
            }
            if (remaining.remove(candidate)) {
              queue.add(candidate);
            }
          }
          letters[i] = original;
        }
      }
      length++;
    }
    return 0;
  }
}
