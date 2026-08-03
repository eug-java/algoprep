package com.algoprep.judge;

import java.util.List;

final class JudgeMainGenerator {
    String generate(JudgeSpec spec) {
        StringBuilder cases = new StringBuilder();
        for (JudgeSpec.JudgeCase testCase : spec.cases()) {
            cases.append(generateCase(spec, testCase));
        }
        return """
                import java.util.*;
                import java.lang.reflect.Array;

                public class JudgeMain {
                  interface Call { Object call() throws Exception; }
                  static void run(String name, Call call, Object expected) {
                    try {
                      Object actual = call.call();
                      boolean pass = same(expected, actual);
                      System.out.println("{\\"name\\":\\"" + json(name) + "\\",\\"pass\\":" + pass
                          + ",\\"expected\\":\\"" + json(display(expected)) + "\\",\\"actual\\":\\""
                          + json(display(actual)) + "\\"}");
                    } catch (Throwable error) {
                      System.out.println("{\\"name\\":\\"" + json(name) + "\\",\\"pass\\":false,\\"expected\\":\\""
                          + json(display(expected)) + "\\",\\"actual\\":\\"\\",\\"message\\":\\""
                          + json(error.getClass().getSimpleName() + ": " + error.getMessage()) + "\\"}");
                    }
                  }
                  static boolean same(Object a, Object b) {
                    if (a instanceof ListNode) a = list((ListNode) a);
                    if (b instanceof ListNode) b = list((ListNode) b);
                    if (a instanceof TreeNode || b instanceof TreeNode) return sameTree(a, b);
                    if (a instanceof Interval && b instanceof Interval) {
                      Interval left = (Interval) a; Interval right = (Interval) b;
                      return left.start == right.start && left.end == right.end;
                    }
                    if (a == null || b == null) return a == b;
                    if (a instanceof Double && b instanceof Number)
                      return Math.abs((Double) a - ((Number) b).doubleValue()) < 1e-6;
                    if (b instanceof Double && a instanceof Number)
                      return Math.abs(((Number) a).doubleValue() - (Double) b) < 1e-6;
                    if (a instanceof List && b instanceof List) {
                      List<?> left = (List<?>) a; List<?> right = (List<?>) b;
                      if (left.size() != right.size()) return false;
                      for (int i = 0; i < left.size(); i++) if (!same(left.get(i), right.get(i))) return false;
                      return true;
                    }
                    if (a.getClass().isArray() && b.getClass().isArray()) {
                      int n = Array.getLength(a); if (n != Array.getLength(b)) return false;
                      for (int i = 0; i < n; i++) if (!same(Array.get(a, i), Array.get(b, i))) return false;
                      return true;
                    }
                    return Objects.equals(a, b);
                  }
                  static boolean sameTree(Object a, Object b) {
                    if (a == null || b == null) return a == b;
                    if (!(a instanceof TreeNode) || !(b instanceof TreeNode)) return false;
                    TreeNode left = (TreeNode) a; TreeNode right = (TreeNode) b;
                    return left.val == right.val && sameTree(left.left, right.left) && sameTree(left.right, right.right);
                  }
                  static List<Integer> list(ListNode node) {
                    List<Integer> values = new ArrayList<>(); int guard = 10_000;
                    while (node != null && guard-- > 0) { values.add(node.val); node = node.next; }
                    return values;
                  }
                  static String display(Object value) {
                    if (value instanceof ListNode) return list((ListNode) value).toString();
                    if (value instanceof TreeNode) return "Tree";
                    if (value instanceof Interval interval) return "[" + interval.start + "," + interval.end + "]";
                    if (value instanceof List<?> values) {
                      List<String> out = new ArrayList<>();
                      for (Object item : values) out.add(display(item));
                      return out.toString();
                    }
                    if (value == null) return "null";
                    if (!value.getClass().isArray()) return String.valueOf(value);
                    int n = Array.getLength(value); List<String> out = new ArrayList<>();
                    for (int i = 0; i < n; i++) out.add(display(Array.get(value, i)));
                    return out.toString();
                  }
                  static String json(String s) {
                    return s.replace("\\\\", "\\\\\\\\").replace("\\"", "\\\\\\"").replace("\\n", "\\\\n").replace("\\r", "\\\\r");
                  }
                  public static void main(String[] args) {
                """ + cases + "  }\n}\n";
    }

    String helperSource(String helper) {
        return switch (helper) {
            case "ListNode" -> "public class ListNode { public int val; public ListNode next; public ListNode() {} public ListNode(int v) { val=v; } public static ListNode of(int... a) { ListNode d=new ListNode(), p=d; for(int v:a) { p.next=new ListNode(v); p=p.next; } return d.next; } }\n";
            case "TreeNode" -> "public class TreeNode { public int val; public TreeNode left,right; public TreeNode() {} public TreeNode(int v) { val=v; } public static TreeNode of(Integer... a) { if(a==null||a.length==0||a[0]==null)return null; TreeNode root=new TreeNode(a[0]); java.util.Queue<TreeNode> q=new java.util.ArrayDeque<>(); q.add(root); for(int i=1;i<a.length;) { TreeNode n=q.remove(); if(i<a.length&&a[i]!=null){n.left=new TreeNode(a[i]);q.add(n.left);}i++;if(i<a.length&&a[i]!=null){n.right=new TreeNode(a[i]);q.add(n.right);}i++; } return root; } }\n";
            case "Interval" -> "public class Interval { public int start,end; public Interval() {} public Interval(int s,int e) { start=s;end=e; } }\n";
            case "GraphNode" -> "import java.util.*; public class GraphNode { public int val; public List<GraphNode> neighbors=new ArrayList<>(); public GraphNode(int v) { val=v; } }\n";
            default -> throw new IllegalArgumentException("Unsupported helper: " + helper);
        };
    }

    private String generateCase(JudgeSpec spec, JudgeSpec.JudgeCase testCase) {
        String name = escape(testCase.name());
        boolean voidReturn = "void".equals(spec.returns());
        boolean hasExpected = testCase.expected() != null;
        boolean hasReference = spec.referenceClass() != null && !spec.referenceClass().isBlank();

        if (voidReturn) {
            return generateVoidCase(spec, testCase, name, hasExpected, hasReference);
        }

        String userCall = userCall(spec, testCase);
        if (hasExpected) {
            return "run(\"" + name + "\", () -> " + userCall + ", "
                    + value(testCase.expected(), spec.returns()) + ");\n";
        }
        if (!hasReference) {
            throw new IllegalArgumentException("Case '" + testCase.name()
                    + "' needs expected or referenceClass");
        }
        return "run(\"" + name + "\", () -> " + userCall + ", "
                + referenceCall(spec, testCase) + ");\n";
    }

    private String generateVoidCase(JudgeSpec spec, JudgeSpec.JudgeCase testCase, String name,
                                    boolean hasExpected, boolean hasReference) {
        if (spec.params() == null || spec.params().isEmpty()) {
            throw new IllegalArgumentException("void judge methods require at least one parameter");
        }
        String arg0Type = spec.params().get(0);
        StringBuilder block = new StringBuilder();
        block.append("{\n");
        if (hasExpected) {
            block.append("  Object __expected = ").append(value(testCase.expected(), arg0Type)).append(";\n");
        } else if (hasReference) {
            for (int i = 0; i < spec.params().size(); i++) {
                block.append("  ").append(javaType(spec.params().get(i))).append(" __ref")
                        .append(i).append(" = ").append(value(testCase.args().get(i), spec.params().get(i)))
                        .append(";\n");
            }
            block.append("  ").append(referenceInvocation(spec, "__ref")).append(";\n");
            block.append("  Object __expected = __ref0;\n");
        } else {
            throw new IllegalArgumentException("void case '" + testCase.name()
                    + "' needs expected or referenceClass");
        }
        block.append("  run(\"").append(name).append("\", () -> {\n");
        for (int i = 0; i < spec.params().size(); i++) {
            block.append("    ").append(javaType(spec.params().get(i))).append(" __arg")
                    .append(i).append(" = ").append(value(testCase.args().get(i), spec.params().get(i)))
                    .append(";\n");
        }
        block.append("    ").append(userInvocation(spec, "__arg")).append(";\n");
        block.append("    return __arg0;\n");
        block.append("  }, __expected);\n");
        block.append("}\n");
        return block.toString();
    }

    private String userCall(JudgeSpec spec, JudgeSpec.JudgeCase testCase) {
        StringBuilder call = new StringBuilder("new ").append(spec.className()).append("().")
                .append(spec.method()).append("(");
        for (int i = 0; i < spec.params().size(); i++) {
            if (i > 0) call.append(", ");
            call.append(value(testCase.args().get(i), spec.params().get(i)));
        }
        return call.append(")").toString();
    }

    private String referenceCall(JudgeSpec spec, JudgeSpec.JudgeCase testCase) {
        StringBuilder args = new StringBuilder();
        for (int i = 0; i < spec.params().size(); i++) {
            if (i > 0) args.append(", ");
            args.append(value(testCase.args().get(i), spec.params().get(i)));
        }
        if (spec.referenceStaticOrDefault()) {
            return spec.referenceClass() + "." + spec.referenceMethodOrDefault() + "(" + args + ")";
        }
        return "new " + spec.referenceClass() + "()." + spec.referenceMethodOrDefault() + "(" + args + ")";
    }

    private String userInvocation(JudgeSpec spec, String argPrefix) {
        StringBuilder call = new StringBuilder("new ").append(spec.className()).append("().")
                .append(spec.method()).append("(");
        for (int i = 0; i < spec.params().size(); i++) {
            if (i > 0) call.append(", ");
            call.append(argPrefix).append(i);
        }
        return call.append(")").toString();
    }

    private String referenceInvocation(JudgeSpec spec, String argPrefix) {
        StringBuilder args = new StringBuilder();
        for (int i = 0; i < spec.params().size(); i++) {
            if (i > 0) args.append(", ");
            args.append(argPrefix).append(i);
        }
        if (spec.referenceStaticOrDefault()) {
            return spec.referenceClass() + "." + spec.referenceMethodOrDefault() + "(" + args + ")";
        }
        return "new " + spec.referenceClass() + "()." + spec.referenceMethodOrDefault() + "(" + args + ")";
    }

    private String javaType(String type) {
        return type;
    }

    String value(Object raw, String type) {
        if (raw == null) return "null";
        if ("int".equals(type) || "long".equals(type) || "double".equals(type) || "boolean".equals(type)
                || "Integer".equals(type) || "Long".equals(type) || "Double".equals(type)
                || "Boolean".equals(type)) {
            return raw.toString();
        }
        if ("String".equals(type)) return "\"" + escape(raw.toString()) + "\"";
        if ("char[][]".equals(type)) {
            List<?> rows = (List<?>) raw;
            return "new char[][]{" + rows.stream()
                    .map(r -> "\"" + escape(r.toString()) + "\".toCharArray()")
                    .reduce((a, b) -> a + "," + b).orElse("") + "}";
        }
        if ("ListNode".equals(type)) return "ListNode.of(" + csv(raw, "int") + ")";
        if ("TreeNode".equals(type)) return "TreeNode.of(" + csv(raw, "Integer") + ")";
        if ("Interval".equals(type)) {
            List<?> pair = (List<?>) raw;
            return "new Interval(" + pair.get(0) + "," + pair.get(1) + ")";
        }
        if ("List<Interval>".equals(type)) {
            return listOf(raw, "Interval");
        }
        if ("List<Integer>".equals(type)) return listOf(raw, "Integer");
        if ("List<String>".equals(type)) return listOf(raw, "String");
        if ("List<List<Integer>>".equals(type)) return listOf(raw, "List<Integer>");
        if ("List<List<String>>".equals(type)) return listOf(raw, "List<String>");
        if ("int[][]".equals(type) || "double[]".equals(type) || type.endsWith("[]")) {
            String element = type.substring(0, type.length() - 2);
            return "new " + type + "{" + csv(raw, element) + "}";
        }
        if (type.startsWith("List<")) {
            String inner = type.substring(5, type.length() - 1);
            return listOf(raw, inner);
        }
        throw new IllegalArgumentException("Unsupported judge type: " + type);
    }

    private String listOf(Object raw, String elementType) {
        List<?> values = (List<?>) raw;
        if (values.isEmpty()) return "java.util.List.of()";
        StringBuilder out = new StringBuilder("java.util.List.of(");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) out.append(',');
            Object value = values.get(i);
            out.append(value == null ? "null" : value(value, elementType));
        }
        return out.append(')').toString();
    }

    private String csv(Object raw, String elementType) {
        List<?> values = (List<?>) raw;
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) out.append(',');
            Object value = values.get(i);
            if (value == null) out.append("null");
            else if ("Integer".equals(elementType) || "int".equals(elementType)
                    || "long".equals(elementType) || "double".equals(elementType)
                    || "boolean".equals(elementType)) {
                out.append(value);
            } else {
                out.append(value(value, elementType));
            }
        }
        return out.toString();
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}
