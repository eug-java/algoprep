package com.algoprep.patterns.trie;
import static org.junit.jupiter.api.Assertions.*; import java.util.List; import org.junit.jupiter.api.Test;
class ReplaceWordsTest { @Test void choosesShortestRoot(){assertEquals("the cat was rat by the bat",ReplaceWords.replaceWords(List.of("cat","bat","rat"),"the cattle was rattled by the battery"));} @Test void leavesUnmatchedWords(){assertEquals("hello",ReplaceWords.replaceWords(List.of("cat"),"hello"));} }
