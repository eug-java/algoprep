package com.algoprep.course.service;

import com.algoprep.course.catalog.PatternQuizLoader;
import com.algoprep.course.i18n.CourseLocale;
import com.algoprep.course.model.PatternId;
import com.algoprep.course.model.PatternQuizQuestion;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PatternQuizService {

    private final PatternQuizLoader quizLoader;
    private final List<PatternQuizLoader.QuizItem> baseItems;
    private final Map<CourseLocale, List<PatternQuizQuestion>> cache = new ConcurrentHashMap<>();

    public PatternQuizService(PatternQuizLoader quizLoader) {
        this.quizLoader = quizLoader;
        this.baseItems = quizLoader.loadItems();
    }

    public List<Map<String, Object>> publicQuestions(CourseLocale locale) {
        return localized(locale).stream().map(q -> {
            Map<String, Object> view = new LinkedHashMap<>();
            view.put("id", q.id());
            view.put("prompt", q.prompt());
            List<PatternId> options = new ArrayList<>(q.options());
            Collections.shuffle(options);
            view.put("options", options);
            return view;
        }).toList();
    }

    public Optional<Map<String, Object>> check(CourseLocale locale, String id, PatternId selected) {
        return localized(locale).stream()
                .filter(q -> q.id().equals(id))
                .findFirst()
                .map(q -> {
                    boolean correct = q.correctPattern() == selected
                            || (q.alsoAccept() != null && q.alsoAccept().contains(selected));
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("id", q.id());
                    result.put("correct", correct);
                    result.put("selected", selected);
                    result.put("correctPattern", q.correctPattern());
                    result.put("alsoAccept", q.alsoAccept() == null ? List.of() : q.alsoAccept());
                    result.put("explanation", q.explanation());
                    return result;
                });
    }

    public int size() {
        return baseItems.size();
    }

    private List<PatternQuizQuestion> localized(CourseLocale locale) {
        return cache.computeIfAbsent(locale, this::build);
    }

    private List<PatternQuizQuestion> build(CourseLocale locale) {
        Map<String, Object> overlay = quizLoader.loadOverlay(locale.code());
        List<PatternQuizQuestion> out = new ArrayList<>();
        for (PatternQuizLoader.QuizItem item : baseItems) {
            Map<String, Object> text = cast(overlay.get(item.id()));
            String prompt = text != null && text.get("prompt") != null
                    ? String.valueOf(text.get("prompt")).trim()
                    : item.id();
            String explanation = text != null && text.get("explanation") != null
                    ? String.valueOf(text.get("explanation")).trim()
                    : "";
            out.add(new PatternQuizQuestion(
                    item.id(),
                    prompt,
                    item.options() == null ? List.of() : item.options(),
                    item.correctPattern(),
                    item.alsoAccept() == null ? List.of() : item.alsoAccept(),
                    explanation
            ));
        }
        return List.copyOf(out);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> cast(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }
}
