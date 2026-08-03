package com.algoprep.course.model;

import java.util.List;

public record ProblemMeta(
    String id,
    String title,
    Difficulty difficulty,
    String className,
    String summary,
    String whenToUse,
    String timeComplexity,
    String spaceComplexity,
    List<String> hints,
    List<String> tags,
    List<String> companies,
    Frequency frequency,
    String walkthroughAscii) {

    /** Convenience for callers that don't attach ASCII walkthroughs. */
    public ProblemMeta withWalkthroughAscii(String ascii) {
        return new ProblemMeta(
                id,
                title,
                difficulty,
                className,
                summary,
                whenToUse,
                timeComplexity,
                spaceComplexity,
                hints,
                tags,
                companies,
                frequency,
                ascii);
    }
}
