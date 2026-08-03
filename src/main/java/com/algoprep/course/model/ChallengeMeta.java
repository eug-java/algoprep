package com.algoprep.course.model;

import java.util.List;

/**
 * A problem presented without pattern labels — used in Challenge Yourself mode.
 */
public record ChallengeMeta(
        String id,
        String title,
        Difficulty difficulty,
        String prompt,
        String className,
        PatternId hiddenPattern,
        String timeComplexity,
        String spaceComplexity,
        List<String> hints
) {
}
