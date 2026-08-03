package com.algoprep.course.i18n;

import java.util.Locale;
import java.util.Optional;

public enum CourseLocale {
    EN("en"),
    RU("ru"),
    ES("es");

    private final String code;

    CourseLocale(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public static CourseLocale from(String raw) {
        if (raw == null || raw.isBlank()) {
            return EN;
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (normalized.contains("-")) {
            normalized = normalized.substring(0, normalized.indexOf('-'));
        }
        return switch (normalized) {
            case "ru", "rus", "russian" -> RU;
            case "es", "spa", "spanish", "es-es", "es-mx" -> ES;
            default -> EN;
        };
    }

    public static Optional<CourseLocale> tryParse(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        CourseLocale locale = from(raw);
        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        if (normalized.startsWith("en") || normalized.startsWith("ru") || normalized.startsWith("es")) {
            return Optional.of(locale);
        }
        // Accept-Language may be complex; from() still maps unknown → EN, so only return if prefix matches
        if (normalized.contains("ru")) return Optional.of(RU);
        if (normalized.contains("es")) return Optional.of(ES);
        if (normalized.contains("en")) return Optional.of(EN);
        return Optional.of(EN);
    }
}
