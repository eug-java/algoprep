package com.algoprep.course.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "algoprep.course")
public record CourseProperties(String title, String language, String version) {}
