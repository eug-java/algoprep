package com.algoprep.course.catalog;

import com.algoprep.course.model.PatternId;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class PatternQuizLoader {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public List<QuizItem> loadItems() {
        QuizDocument doc = read("course/pattern-quiz.yml", QuizDocument.class);
        return doc.questions() == null ? List.of() : List.copyOf(doc.questions());
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> loadOverlay(String localeCode) {
        Map<String, Object> doc = read("course/i18n/" + localeCode + "/pattern-quiz.yml", Map.class);
        Object questions = doc.get("questions");
        if (questions instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    private <T> T read(String path, Class<T> type) {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return yamlMapper.readValue(in, type);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + path, e);
        }
    }

    public record QuizDocument(List<QuizItem> questions) {
    }

    public record QuizItem(
            String id,
            PatternId correctPattern,
            List<PatternId> options
    ) {
    }
}
