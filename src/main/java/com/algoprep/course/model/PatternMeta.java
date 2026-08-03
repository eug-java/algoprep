package com.algoprep.course.model;

import java.util.List;

public record PatternMeta(
        PatternId id,
        int order,
        int week,
        String track,
        String title,
        String subtitle,
        String intuition,
        String recognition,
        String walkthrough,
        List<String> telltaleSigns,
        List<String> templateSteps,
        List<String> commonMistakes,
        List<String> whenNotToUse,
        List<ProblemMeta> problems
) {
}
