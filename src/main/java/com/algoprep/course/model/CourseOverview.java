package com.algoprep.course.model;

import java.util.List;

public record CourseOverview(
        String title,
        String language,
        String version,
        String description,
        int patternCount,
        int problemCount,
        List<WeekSummary> weeks,
        List<PatternSummary> patterns
) {
    public record PatternSummary(
            PatternId id,
            int order,
            int week,
            String title,
            int problemCount,
            String subtitle
    ) {
    }

    public record WeekSummary(
            int week,
            String title,
            List<PatternId> patternIds
    ) {
    }
}
