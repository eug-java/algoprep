package com.algoprep.judge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JudgeServiceTest {
    @Test
    void judgesCorrectPairWithTargetSumSolution() {
        String source = """
                public class Solution {
                    public int[] search(int[] values, int target) {
                        int left = 0, right = values.length - 1;
                        while (left < right) {
                            int sum = values[left] + values[right];
                            if (sum == target) return new int[] { left, right };
                            if (sum < target) left++; else right--;
                        }
                        return new int[] { -1, -1 };
                    }
                }
                """;

        JudgeResult result = new JudgeService().judge(
                new JudgeRequest("pair-with-target-sum", "TWO_POINTERS", source, "en"));

        assertTrue(result.ok(), result.compileErrors() + result.runtimeErrors());
        assertEquals(2, result.passed());
        assertEquals(2, result.total());
    }

    @Test
    void judgesCorrectMinStackOpsSolution() {
        String source = """
                import java.util.ArrayDeque;
                import java.util.Deque;

                public class Solution {
                    private final Deque<Integer> values = new ArrayDeque<>();
                    private final Deque<Integer> minimums = new ArrayDeque<>();

                    public void push(int value) {
                        values.push(value);
                        if (minimums.isEmpty() || value <= minimums.peek()) minimums.push(value);
                    }

                    public void pop() {
                        int value = values.pop();
                        if (value == minimums.peek()) minimums.pop();
                    }

                    public int top() {
                        return values.peek();
                    }

                    public int getMin() {
                        return minimums.peek();
                    }
                }
                """;

        JudgeResult result = new JudgeService().judge(
                new JudgeRequest("min-stack", "CUSTOM_DATA_STRUCTURES", source, "en"));

        assertTrue(result.ok(), result.compileErrors() + result.runtimeErrors());
        assertEquals(1, result.passed());
        assertEquals(1, result.total());
    }

    @Test
    void rejectsFileReads() {
        assertThrows(IllegalArgumentException.class, () -> new JudgeService().judge(
                new JudgeRequest("two-sum", "HASH_MAPS", "FileInputStream in;", "en")));
    }

    @Test
    void judgesLongestIncreasingSubsequence() {
        String source = """
                public class Solution {
                    public int lengthOfLIS(int[] nums) {
                        int[] dp = new int[nums.length];
                        java.util.Arrays.fill(dp, 1);
                        int best = 1;
                        for (int i = 0; i < nums.length; i++) {
                            for (int j = 0; j < i; j++) if (nums[j] < nums[i]) dp[i] = Math.max(dp[i], dp[j] + 1);
                            best = Math.max(best, dp[i]);
                        }
                        return best;
                    }
                }
                """;
        JudgeResult result = new JudgeService().judge(
                new JudgeRequest("longest-increasing-subsequence", "DYNAMIC_PROGRAMMING", source, "en"));
        assertTrue(result.ok(), result.compileErrors() + result.runtimeErrors());
    }

    @Test
    void judgesLowestCommonAncestorByValue() {
        String source = """
                public class Solution {
                    public int lowestCommonAncestor(TreeNode root, int p, int q) {
                        TreeNode ancestor = search(root, p, q);
                        if (ancestor == null) throw new IllegalStateException("missing");
                        return ancestor.val;
                    }
                    private TreeNode search(TreeNode node, int p, int q) {
                        if (node == null || node.val == p || node.val == q) return node;
                        TreeNode left = search(node.left, p, q);
                        TreeNode right = search(node.right, p, q);
                        if (left != null && right != null) return node;
                        return left != null ? left : right;
                    }
                }
                """;
        JudgeResult result = new JudgeService().judge(
                new JudgeRequest("lowest-common-ancestor", "TREE_DFS", source, "en"));
        assertTrue(result.ok(), result.compileErrors() + result.runtimeErrors());
        assertEquals(2, result.passed());
    }

    @Test
    void judgesDelegatedNewSpecs() {
        judgeOk("task-scheduler", "CHALLENGE", """
                public class Solution {
                    public int leastInterval(char[] tasks, int n) {
                        return com.algoprep.patterns.challenge.TaskScheduler.leastInterval(tasks, n);
                    }
                }
                """);
        judgeOk("palindrome-pairs", "CHALLENGE", """
                public class Solution {
                    public java.util.List<java.util.List<Integer>> palindromePairs(String[] words) {
                        return com.algoprep.patterns.challenge.PalindromePairs.palindromePairs(words);
                    }
                }
                """);
        judgeOk("count-of-smaller", "CHALLENGE", """
                public class Solution {
                    public java.util.List<Integer> countSmaller(int[] nums) {
                        return com.algoprep.patterns.challenge.CountOfSmallerNumbers.countSmaller(nums);
                    }
                }
                """);
        judgeOk("clone-graph", "GRAPHS", """
                public class Solution {
                    public int[][] cloneGraph(int[][] adj) {
                        return com.algoprep.patterns.graphs.CloneGraph.cloneGraph(adj);
                    }
                }
                """);
        judgeOk("is-graph-bipartite", "GRAPHS", """
                public class Solution {
                    public boolean isBipartite(int[][] graph) {
                        return com.algoprep.patterns.graphs.IsGraphBipartite.isBipartite(graph);
                    }
                }
                """);
        judgeOk("unique-paths", "DYNAMIC_PROGRAMMING", """
                public class Solution {
                    public int uniquePaths(int m, int n) {
                        return com.algoprep.patterns.dp.UniquePaths.uniquePaths(m, n);
                    }
                }
                """);
        judgeOk("employee-free-time", "CHALLENGE", """
                import java.util.*;
                public class Solution {
                    public List<Interval> employeeFreeTime(List<List<Interval>> schedule) {
                        List<Interval> busy = new ArrayList<>();
                        for (List<Interval> employee : schedule) busy.addAll(employee);
                        busy.sort((a, b) -> Integer.compare(a.start, b.start));
                        List<Interval> free = new ArrayList<>();
                        int end = busy.get(0).end;
                        for (int i = 1; i < busy.size(); i++) {
                            Interval next = busy.get(i);
                            if (next.start > end) free.add(new Interval(end, next.start));
                            end = Math.max(end, next.end);
                        }
                        return free;
                    }
                }
                """);
        judgeOk("serialize-binary-tree", "CHALLENGE", """
                import java.util.*;
                public class Solution {
                    public TreeNode roundTrip(TreeNode root) {
                        return parse(new ArrayDeque<>(Arrays.asList(write(root).split(",", -1))));
                    }
                    private String write(TreeNode node) {
                        if (node == null) return "#";
                        return node.val + "," + write(node.left) + "," + write(node.right);
                    }
                    private TreeNode parse(Deque<String> tokens) {
                        String token = tokens.remove();
                        if (token.isEmpty() || "#".equals(token)) return null;
                        TreeNode node = new TreeNode(Integer.parseInt(token));
                        node.left = parse(tokens);
                        node.right = parse(tokens);
                        return node;
                    }
                }
                """);
    }

    private static void judgeOk(String problemId, String patternId, String source) {
        JudgeResult result = new JudgeService().judge(new JudgeRequest(problemId, patternId, source, "en"));
        assertTrue(result.ok(), problemId + " " + result.compileErrors() + result.runtimeErrors());
    }
}
