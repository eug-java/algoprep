package com.algoprep.judge;

import java.util.List;

public record JudgeResult(
        boolean ok,
        int passed,
        int total,
        List<JudgeFailure> failures,
        String compileErrors,
        String runtimeErrors,
        long durationMs) {

    public record JudgeFailure(String name, String expected, String actual, String message) {
    }
}
