package com.algoprep.course.catalog;

import com.algoprep.course.model.Difficulty;
import com.algoprep.course.model.Frequency;
import com.algoprep.course.model.PatternId;
import com.algoprep.course.model.PatternMeta;
import com.algoprep.course.model.ProblemMeta;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CourseCatalogLoader {

    private final ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());

    public CatalogDocument loadBase() {
        return read("course/catalog.yml", CatalogDocument.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> loadPatternOverlay(String localeCode) {
        Map<String, Object> doc = read("course/i18n/" + localeCode + "/patterns.yml", Map.class);
        Object patterns = doc.get("patterns");
        if (patterns instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Map.of();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> loadUiBundle(String localeCode) {
        return read("course/i18n/" + localeCode + "/ui.yml", Map.class);
    }

    private <T> T read(String path, Class<T> type) {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            return yamlMapper.readValue(in, type);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + path, e);
        }
    }

    public record CatalogDocument(String description, List<PatternDocument> patterns) {
    }

    public record PatternDocument(
            PatternId id,
            int order,
            Integer week,
            String track,
            String title,
            String subtitle,
            String intuition,
            String recognition,
            String walkthrough,
            List<String> telltaleSigns,
            List<String> templateSteps,
            List<String> commonMistakes,
            List<String> whenNotToUse,
            String constraints,
            String followUp,
            List<ProblemDocument> problems
    ) {
        public PatternMeta toMeta() {
            return toMeta(null);
        }

        @SuppressWarnings("unchecked")
        public PatternMeta toMeta(Map<String, Object> overlay) {
            String title = this.title;
            String subtitle = this.subtitle;
            String intuition = this.intuition;
            String recognition = this.recognition;
            String walkthrough = this.walkthrough;
            List<String> telltaleSigns = this.telltaleSigns;
            List<String> templateSteps = this.templateSteps;
            List<String> commonMistakes = this.commonMistakes;
            List<String> whenNotToUse = this.whenNotToUse;
            String constraints = this.constraints;
            String followUp = this.followUp;

            Map<String, Object> problemOverlays = Map.of();
            if (overlay != null) {
                title = str(overlay.get("title"), title);
                subtitle = str(overlay.get("subtitle"), subtitle);
                intuition = str(overlay.get("intuition"), intuition);
                recognition = str(overlay.get("recognition"), recognition);
                walkthrough = str(overlay.get("walkthrough"), walkthrough);
                telltaleSigns = list(overlay.get("telltaleSigns"), telltaleSigns);
                templateSteps = list(overlay.get("templateSteps"), templateSteps);
                commonMistakes = list(overlay.get("commonMistakes"), commonMistakes);
                whenNotToUse = list(overlay.get("whenNotToUse"), whenNotToUse);
                constraints = str(overlay.get("constraints"), constraints);
                followUp = str(overlay.get("followUp"), followUp);
                Object probs = overlay.get("problems");
                if (probs instanceof Map<?, ?> m) {
                    problemOverlays = (Map<String, Object>) m;
                }
            }

            List<ProblemMeta> problemMetas = new ArrayList<>();
            if (problems != null) {
                for (ProblemDocument problem : problems) {
                    Map<String, Object> po = castMap(problemOverlays.get(problem.id()));
                    problemMetas.add(problem.toMeta(po));
                }
            }

            return new PatternMeta(
                    id,
                    order,
                    week == null ? defaultWeek(order) : week,
                    track,
                    title,
                    subtitle,
                    intuition,
                    recognition,
                    walkthrough == null ? "" : walkthrough,
                    telltaleSigns == null ? List.of() : telltaleSigns,
                    templateSteps == null ? List.of() : templateSteps,
                    commonMistakes == null ? List.of() : commonMistakes,
                    whenNotToUse == null ? List.of() : whenNotToUse,
                    constraints == null ? "" : constraints,
                    followUp == null ? "" : followUp,
                    problemMetas
            );
        }

        private static int defaultWeek(int order) {
            if (order <= 8) return 1;
            if (order <= 15) return 2;
            if (order <= 22) return 3;
            return 4;
        }
    }

    public record ProblemDocument(
            String id,
            String title,
            Difficulty difficulty,
            String className,
            String summary,
            String whenToUse,
            String timeComplexity,
            String spaceComplexity,
            List<String> hints,
            List<String> tags,
            List<String> companies,
            Frequency frequency,
            String example,
            String constraints,
            String followUp,
            Boolean discussionOnly,
            String failureNote
    ) {
        public ProblemMeta toMeta() {
            return toMeta(null);
        }

        public ProblemMeta toMeta(Map<String, Object> overlay) {
            String title = this.title;
            String summary = this.summary;
            String whenToUse = this.whenToUse;
            List<String> hints = this.hints;
            String example = this.example;
            String constraints = this.constraints;
            String followUp = this.followUp;
            String failureNote = this.failureNote;
            if (overlay != null) {
                title = str(overlay.get("title"), title);
                summary = str(overlay.get("summary"), summary);
                whenToUse = str(overlay.get("whenToUse"), whenToUse);
                hints = list(overlay.get("hints"), hints);
                example = str(overlay.get("example"), example);
                constraints = str(overlay.get("constraints"), constraints);
                followUp = str(overlay.get("followUp"), followUp);
                failureNote = str(overlay.get("failureNote"), failureNote);
            }
            return new ProblemMeta(
                    id,
                    title,
                    difficulty,
                    className,
                    summary,
                    whenToUse,
                    timeComplexity,
                    spaceComplexity,
                    hints == null ? List.of() : hints,
                    tags == null ? List.of() : tags,
                    companies == null ? List.of() : companies,
                    frequency,
                    example,
                    constraints,
                    followUp,
                    discussionOnly,
                    failureNote,
                    null
            );
        }
    }

    public Map<String, String> loadWalkthroughs() {
        return loadWalkthroughs("en");
    }

    public Map<String, String> loadWalkthroughs(String localeCode) {
        Map<String, String> base = readWalkthroughs("course/walkthroughs.yml");
        if (localeCode == null || localeCode.isBlank() || "en".equals(localeCode)) {
            return base;
        }
        Map<String, String> merged = new java.util.LinkedHashMap<>(base);
        readWalkthroughs("course/i18n/" + localeCode + "/walkthroughs.yml")
                .forEach((key, value) -> {
                    if (value != null && !value.isBlank()) {
                        merged.put(key, value);
                    }
                });
        return merged;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> readWalkthroughs(String path) {
        try {
            Map<String, Object> doc = read(path, Map.class);
            Map<String, String> out = new java.util.LinkedHashMap<>();
            if (doc != null) {
                doc.forEach((k, v) -> {
                    if (v != null) out.put(String.valueOf(k), String.valueOf(v));
                });
            }
            return out;
        } catch (IllegalStateException e) {
            return Map.of();
        }
    }

    private static String str(Object value, String fallback) {
        if (value == null) return fallback;
        String s = String.valueOf(value).trim();
        return s.isEmpty() ? fallback : s;
    }

    @SuppressWarnings("unchecked")
    private static List<String> list(Object value, List<String> fallback) {
        if (value instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return fallback;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }
}
