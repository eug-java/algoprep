package com.algoprep.course.api;

import com.algoprep.course.model.PatternId;
import jakarta.validation.constraints.NotNull;

public record QuizAnswerRequest(@NotNull PatternId selectedPattern) {
}
