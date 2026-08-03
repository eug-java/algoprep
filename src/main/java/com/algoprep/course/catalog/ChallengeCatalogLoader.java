package com.algoprep.course.catalog;

import com.algoprep.course.model.ChallengeMeta;
import com.algoprep.course.model.Difficulty;
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
public class ChallengeCatalogLoader {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public List<ChallengeItem> loadItems() {
        ChallengeDocument doc = read("course/challenges.yml", ChallengeDocument.class);
        return doc.challenges() == null ? List.of() : List.copyOf(doc.challenges());
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> loadOverlay(String localeCode) {
        Map<String, Object> doc = read("course/i18n/" + localeCode + "/challenges.yml", Map.class);
        Object challenges = doc.get("challenges");
        if (challenges instanceof Map<?, ?> map) {
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

    public record ChallengeDocument(List<ChallengeItem> challenges) {
    }

    public record ChallengeItem(
            String id,
            String title,
            Difficulty difficulty,
            String prompt,
            String className,
            PatternId hiddenPattern,
            String timeComplexity,
            String spaceComplexity,
            List<String> hints
    ) {
        public ChallengeMeta toMeta(Map<String, Object> overlay) {
            String localizedTitle = title;
            String localizedPrompt = prompt;
            List<String> localizedHints = hints;
            if (overlay != null) {
                if (overlay.get("title") != null) {
                    localizedTitle = String.valueOf(overlay.get("title"));
                }
                if (overlay.get("prompt") != null) {
                    localizedPrompt = String.valueOf(overlay.get("prompt"));
                }
                if (overlay.get("hints") instanceof List<?> list) {
                    localizedHints = list.stream().map(String::valueOf).toList();
                }
            }
            return new ChallengeMeta(
                    id,
                    localizedTitle,
                    difficulty,
                    localizedPrompt,
                    className,
                    hiddenPattern,
                    timeComplexity,
                    spaceComplexity,
                    localizedHints == null ? List.of() : localizedHints
            );
        }
    }
}
