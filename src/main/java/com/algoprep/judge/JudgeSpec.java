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
        List<JudgeCase> cases) {

    record JudgeCase(String name, List<Object> args, Object expected) {
    }

    boolean referenceStaticOrDefault() {
        return referenceStatic == null || referenceStatic;
    }

    String referenceMethodOrDefault() {
        return referenceMethod == null || referenceMethod.isBlank() ? method : referenceMethod;
    }
}
