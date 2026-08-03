package com.algoprep.course.model;

import java.util.List;

public record CourseOverview(
        String title,
        String language,
        String version,
        String description,
        int patternCount,
        int problemCount,
        int easyCount,
        int mediumCount,
        int hardCount,
        List<WeekSummary> weeks,
        List<PatternSummary> patterns
) {
    public record PatternSummary(
            PatternId id,
            int order,
            int week,
            String title,
            int problemCount,
            int easyCount,
            int mediumCount,
            int hardCount,
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
