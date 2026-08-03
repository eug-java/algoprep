package com.algoprep.course.service;

import com.algoprep.course.model.SourceSnippet;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class SourceService {

    public Optional<SourceSnippet> loadByClassName(String className) {
        if (className == null || className.isBlank()) {
            return Optional.empty();
        }
        String relative = className.replace('.', '/') + ".java";

        Path local = Path.of("src/main/java", relative);
        if (Files.isRegularFile(local)) {
            try {
                return Optional.of(new SourceSnippet(
                        className,
                        local.toString(),
                        "java",
                        Files.readString(local, StandardCharsets.UTF_8)
                ));
            } catch (IOException ignored) {
                // fall through to classpath
            }
        }

        Resource classpath = new ClassPathResource("course/sources/" + relative);
        if (classpath.exists()) {
            try {
                return Optional.of(new SourceSnippet(
                        className,
                        "classpath:course/sources/" + relative,
                        "java",
                        classpath.getContentAsString(StandardCharsets.UTF_8)
                ));
            } catch (IOException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}
