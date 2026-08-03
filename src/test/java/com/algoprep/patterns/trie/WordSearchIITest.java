package com.algoprep.patterns.trie;
import static org.junit.jupiter.api.Assertions.*; import java.util.List; import org.junit.jupiter.api.Test;
class WordSearchIITest { @Test void findsOnlyReachableWords(){char[][] board={{'o','a','a','n'},{'e','t','a','e'},{'i','h','k','r'},{'i','f','l','v'}};assertEquals(List.of("oath","eat"),WordSearchII.findWords(board,new String[]{"oath","pea","eat","rain"}));} @Test void removesDuplicates(){assertEquals(List.of("a"),WordSearchII.findWords(new char[][]{{'a'}},new String[]{"a","a"}));} }
