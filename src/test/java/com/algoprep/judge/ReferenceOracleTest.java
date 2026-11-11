package com.algoprep.judge;

import com.algoprep.common.Interval;
import com.algoprep.common.ListNode;
import com.algoprep.common.TreeNode;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every non-ops judge spec that names a reference method must agree with that method
 * on the cases shipped in the spec.
 */
class ReferenceOracleTest {

    @Test
    void referenceMethodsMatchSpecCases() throws Exception {
        JudgeService judge = new JudgeService();
        List<String> ids = specIds();
        assertTrue(ids.size() > 100, "expected the project judge specs, found " + ids.size());
        int checked = 0;
        List<String> unloadable = new ArrayList<>();
        List<String> disagreements = new ArrayList<>();
        for (String id : ids) {
            JudgeSpec spec = judge.loadSpec(id);
            if (spec.referenceClass() == null || spec.opsMode()) continue;
            Class<?> type;
            Method method;
            try {
                type = Class.forName(spec.referenceClass());
                method = findMethod(type, spec.referenceMethodOrDefault(), spec.params().size());
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                unloadable.add(id + " " + ex.getMessage());
                continue;
            }
            Object receiver = java.lang.reflect.Modifier.isStatic(method.getModifiers())
                    ? null
                    : type.getDeclaredConstructor().newInstance();
            for (JudgeSpec.JudgeCase testCase : spec.cases()) {
                Object[] args = new Object[spec.params().size()];
                for (int i = 0; i < args.length; i++) {
                    args[i] = convert(testCase.args().get(i), spec.params().get(i));
                }
                Object actual;
                try {
                    actual = method.invoke(receiver, args);
                } catch (java.lang.reflect.InvocationTargetException ex) {
                    disagreements.add(id + ":" + testCase.name() + " reference threw " + ex.getCause());
                    continue;
                }
                try {
                    if ("void".equals(spec.returns())) {
                        if (testCase.expected() != null) {
                            Object expected = convert(testCase.expected(), spec.params().get(0));
                            assertEqual(id + ":" + testCase.name(), expected, args[0]);
                        }
                    } else if (testCase.expected() != null) {
                        Object expected = convert(testCase.expected(), spec.returns());
                        assertEqual(id + ":" + testCase.name(), expected, actual);
                    }
                } catch (AssertionError ex) {
                    disagreements.add(id + ":" + testCase.name() + " " + ex.getMessage());
                    continue;
                }
                checked++;
            }
        }
        assertTrue(unloadable.isEmpty(), String.join("\n", unloadable));
        assertTrue(disagreements.isEmpty(), String.join("\n", disagreements));
        assertTrue(checked > 50, "reference oracle checked too few cases: " + checked);
    }

    private static List<String> specIds() throws Exception {
        java.nio.file.Path dir = java.nio.file.Path.of("src/main/resources/judge/specs");
        try (var files = java.nio.file.Files.list(dir)) {
            return files.filter(path -> path.getFileName().toString().endsWith(".yml"))
                    .map(path -> path.getFileName().toString().replace(".yml", ""))
                    .sorted()
                    .toList();
        }
    }

    private static Method findMethod(Class<?> type, String name, int arity) throws NoSuchMethodException {
        for (Method method : type.getDeclaredMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == arity) {
                method.setAccessible(true);
                return method;
            }
        }
        throw new NoSuchMethodException(type.getName() + "." + name + "/" + arity);
    }

    private static Object convert(Object raw, String type) {
        if (raw == null || "String".equals(type)) return raw;
        if ("int".equals(type) || "Integer".equals(type)) return ((Number) raw).intValue();
        if ("long".equals(type) || "Long".equals(type)) return ((Number) raw).longValue();
        if ("double".equals(type) || "Double".equals(type)) return ((Number) raw).doubleValue();
        if ("boolean".equals(type) || "Boolean".equals(type)) return raw;
        if ("ListNode".equals(type)) return toListNode(raw);
        if ("ListNode[]".equals(type)) {
            List<?> lists = (List<?>) raw;
            ListNode[] nodes = new ListNode[lists.size()];
            for (int i = 0; i < lists.size(); i++) nodes[i] = toListNode(lists.get(i));
            return nodes;
        }
        if ("TreeNode".equals(type)) return toTree(raw);
        if ("Interval".equals(type)) return toInterval(raw);
        if ("Interval[]".equals(type) || "List<Interval>".equals(type)) {
            List<Interval> intervals = toIntervals(raw);
            return "Interval[]".equals(type) ? intervals.toArray(Interval[]::new) : intervals;
        }
        if ("List<List<Interval>>".equals(type)) {
            List<?> rows = (List<?>) raw;
            List<List<Interval>> out = new ArrayList<>();
            for (Object row : rows) out.add(toIntervals(row));
            return out;
        }
        if ("char[][]".equals(type)) {
            List<?> rows = (List<?>) raw;
            char[][] board = new char[rows.size()][];
            for (int i = 0; i < rows.size(); i++) board[i] = String.valueOf(rows.get(i)).toCharArray();
            return board;
        }
        if ("int[]".equals(type)) return toIntArray(raw);
        if ("long[]".equals(type)) {
            List<?> values = (List<?>) raw;
            long[] out = new long[values.size()];
            for (int i = 0; i < values.size(); i++) out[i] = ((Number) values.get(i)).longValue();
            return out;
        }
        if ("double[]".equals(type)) {
            List<?> values = (List<?>) raw;
            double[] out = new double[values.size()];
            for (int i = 0; i < values.size(); i++) out[i] = ((Number) values.get(i)).doubleValue();
            return out;
        }
        if ("String[]".equals(type)) {
            List<?> values = (List<?>) raw;
            return values.stream().map(String::valueOf).toArray(String[]::new);
        }
        if ("int[][]".equals(type)) {
            List<?> rows = (List<?>) raw;
            int[][] out = new int[rows.size()][];
            for (int i = 0; i < rows.size(); i++) out[i] = (int[]) convert(rows.get(i), "int[]");
            return out;
        }
        if ("char[]".equals(type)) return String.valueOf(raw).toCharArray();
        if (type.startsWith("List")) return raw;
        throw new IllegalArgumentException("Unsupported oracle type " + type);
    }

    private static ListNode toListNode(Object raw) {
        if (raw == null) return null;
        int[] values = toIntArray(raw);
        return ListNode.of(values);
    }

    private static TreeNode toTree(Object raw) {
        if (raw == null) return null;
        List<?> values = (List<?>) raw;
        Integer[] nodes = new Integer[values.size()];
        for (int i = 0; i < values.size(); i++) {
            nodes[i] = values.get(i) == null ? null : ((Number) values.get(i)).intValue();
        }
        return TreeNode.of(nodes);
    }

    private static Interval toInterval(Object raw) {
        List<?> pair = (List<?>) raw;
        return new Interval(((Number) pair.get(0)).intValue(), ((Number) pair.get(1)).intValue());
    }

    private static List<Interval> toIntervals(Object raw) {
        List<?> rows = (List<?>) raw;
        List<Interval> out = new ArrayList<>();
        for (Object row : rows) out.add(toInterval(row));
        return out;
    }

    private static int[] toIntArray(Object raw) {
        List<?> values = (List<?>) raw;
        int[] out = new int[values.size()];
        for (int i = 0; i < values.size(); i++) out[i] = ((Number) values.get(i)).intValue();
        return out;
    }

    private static void assertEqual(String name, Object expected, Object actual) {
        if (expected instanceof int[] left && actual instanceof int[] right) {
            org.junit.jupiter.api.Assertions.assertArrayEquals(left, right, name);
            return;
        }
        if (expected instanceof long[] left && actual instanceof long[] right) {
            org.junit.jupiter.api.Assertions.assertArrayEquals(left, right, name);
            return;
        }
        if (expected instanceof int[][] left && actual instanceof int[][] right) {
            assertEquals(left.length, right.length, name);
            for (int i = 0; i < left.length; i++) {
                org.junit.jupiter.api.Assertions.assertArrayEquals(left[i], right[i], name);
            }
            return;
        }
        if (expected instanceof char[][] left && actual instanceof char[][] right) {
            assertEquals(left.length, right.length, name);
            for (int i = 0; i < left.length; i++) {
                org.junit.jupiter.api.Assertions.assertArrayEquals(left[i], right[i], name);
            }
            return;
        }
        if (expected instanceof ListNode || actual instanceof ListNode) {
            assertEquals(ListNode.toList((ListNode) expected), ListNode.toList((ListNode) actual), name);
            return;
        }
        if (expected instanceof TreeNode || actual instanceof TreeNode) {
            assertEquals(levelOrder(expected instanceof TreeNode node ? node : null),
                    levelOrder(actual instanceof TreeNode node ? node : null), name);
            return;
        }
        if (expected instanceof List<?> left && actual instanceof List<?> right) {
            assertEquals(normalize(left), normalize(right), name);
            return;
        }
        if (expected instanceof List<?> left && actual != null && actual.getClass().isArray()) {
            assertEquals(normalize(left), normalize(arrayToList(actual)), name);
            return;
        }
        assertEquals(expected, actual, name);
    }

    private static Object normalize(Object value) {
        if (value instanceof Interval interval) return List.of(interval.start(), interval.end());
        if (value instanceof List<?> list) {
            List<Object> out = new ArrayList<>();
            for (Object item : list) out.add(normalize(item));
            return out;
        }
        if (value instanceof Number number && !(value instanceof Double)) return number.intValue();
        return value;
    }

    private static List<Integer> levelOrder(TreeNode root) {
        List<Integer> out = new ArrayList<>();
        if (root == null) return out;
        Queue<TreeNode> pending = new ArrayDeque<>();
        pending.add(root);
        while (!pending.isEmpty()) {
            TreeNode node = pending.poll();
            if (node == null) {
                out.add(null);
                continue;
            }
            out.add(node.val);
            pending.add(node.left);
            pending.add(node.right);
        }
        int end = out.size();
        while (end > 0 && out.get(end - 1) == null) end--;
        return out.subList(0, end);
    }

    private static List<Object> arrayToList(Object array) {
        int length = java.lang.reflect.Array.getLength(array);
        List<Object> out = new ArrayList<>();
        for (int i = 0; i < length; i++) out.add(normalize(java.lang.reflect.Array.get(array, i)));
        return out;
    }
}
