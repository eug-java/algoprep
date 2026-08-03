package com.algoprep.course.model;

public record SourceSnippet(
        String className,
        String filePath,
        String language,
        String source
) {
}
