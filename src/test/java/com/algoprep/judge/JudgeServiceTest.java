package com.algoprep.judge;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
