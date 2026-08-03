package com.algoprep.course.model;

import java.util.List;

public record PatternQuizQuestion(
        String id,
        String prompt,
        List<PatternId> options,
        PatternId correctPattern,
        String explanation
) {
}
