package com.algoprep.judge;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JudgeRequest(
        @NotBlank String problemId,
        @NotBlank String patternId,
        @NotBlank @Size(max = 50_000) String source,
        String lang) {
}
