package com.algoprep.judge;

import java.util.List;

record JudgeSpec(
        String className,
        String method,
        List<String> params,
        String returns,
        String referenceClass,
        String referenceMethod,
        Boolean referenceStatic,
        List<String> imports,
        List<String> helpers,
        String mode,
        List<MethodSig> methods,
        List<String> constructorParams,
        List<JudgeCase> cases) {

    record MethodSig(String name, List<String> params, String returns) {
    }

    record JudgeCase(
            String name,
            List<Object> args,
            Object expected,
            List<Object> constructorArgs,
            List<List<Object>> ops) {
    }

    boolean referenceStaticOrDefault() {
        return referenceStatic == null || referenceStatic;
    }

    String referenceMethodOrDefault() {
        return referenceMethod == null || referenceMethod.isBlank() ? method : referenceMethod;
    }

    boolean opsMode() {
        if ("ops".equals(mode)) return true;
        if (cases == null) return false;
        for (JudgeCase testCase : cases) {
            if (testCase.ops() != null) return true;
        }
        return false;
    }
}
