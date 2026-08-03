package com.algoprep.course.service;

import com.algoprep.course.catalog.ChallengeCatalogLoader;
import com.algoprep.course.catalog.CourseCatalogLoader;
import com.algoprep.course.config.CourseProperties;
import com.algoprep.course.i18n.CourseLocale;
import com.algoprep.course.model.ChallengeMeta;
import com.algoprep.course.model.CourseOverview;
import com.algoprep.course.model.PatternId;
import com.algoprep.course.model.PatternMeta;
import com.algoprep.course.model.ProblemMeta;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CourseService {

    private final CourseProperties properties;
    private final CourseCatalogLoader catalogLoader;
    private final ChallengeCatalogLoader challengeLoader;
    private final CourseCatalogLoader.CatalogDocument baseCatalog;
    private final List<ChallengeCatalogLoader.ChallengeItem> baseChallenges;
    private final Map<String, String> walkthroughs;
    private final Map<CourseLocale, LocalizedBundle> cache = new ConcurrentHashMap<>();

    public CourseService(
            CourseProperties properties,
            CourseCatalogLoader catalogLoader,
            ChallengeCatalogLoader challengeLoader) {
        this.properties = properties;
        this.catalogLoader = catalogLoader;
        this.challengeLoader = challengeLoader;
        this.baseCatalog = catalogLoader.loadBase();
        this.baseChallenges = challengeLoader.loadItems();
        this.walkthroughs = catalogLoader.loadWalkthroughs();
    }

    public CourseOverview overview(CourseLocale locale) {
        LocalizedBundle bundle = bundle(locale);
        List<CourseOverview.PatternSummary> summaries = bundle.patterns().values().stream()
                .sorted(Comparator.comparingInt(PatternMeta::order))
                .map(p -> new CourseOverview.PatternSummary(
                        p.id(), p.order(), p.week(), p.title(), p.problems().size(), p.subtitle()))
                .toList();

        int problemCount = summaries.stream().mapToInt(CourseOverview.PatternSummary::problemCount).sum()
                + bundle.challenges().size();

        return new CourseOverview(
                properties.title(),
                locale.code(),
                properties.version(),
                bundle.courseDescription(),
                summaries.size(),
                problemCount,
                List.of(),
                summaries
        );
    }

    public List<PatternMeta> listPatterns(CourseLocale locale) {
        return bundle(locale).patterns().values().stream()
                .sorted(Comparator.comparingInt(PatternMeta::order))
                .toList();
    }

    public Optional<PatternMeta> getPattern(CourseLocale locale, PatternId id) {
        return Optional.ofNullable(bundle(locale).patterns().get(id));
    }

    public Optional<ProblemMeta> getProblem(CourseLocale locale, PatternId patternId, String problemId) {
        return getPattern(locale, patternId)
                .flatMap(p -> p.problems().stream().filter(pr -> pr.id().equals(problemId)).findFirst())
                .map(this::attachWalkthrough);
    }

    public Optional<String> getWalkthrough(String problemId) {
        String text = walkthroughs.get(problemId);
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(text);
    }

    private ProblemMeta attachWalkthrough(ProblemMeta problem) {
        String ascii = walkthroughs.get(problem.id());
        if (ascii == null || ascii.isBlank()) {
            return problem;
        }
        return problem.withWalkthroughAscii(ascii);
    }

    public List<ChallengeMeta> listChallenges(CourseLocale locale) {
        return bundle(locale).challenges();
    }

    public Optional<ChallengeMeta> getChallenge(CourseLocale locale, String id) {
        return bundle(locale).challenges().stream().filter(c -> c.id().equals(id)).findFirst();
    }

    public Map<String, Object> uiBundle(CourseLocale locale) {
        return bundle(locale).ui();
    }

    private LocalizedBundle bundle(CourseLocale locale) {
        return cache.computeIfAbsent(locale, this::buildBundle);
    }

    private LocalizedBundle buildBundle(CourseLocale locale) {
        Map<String, Object> patternOverlay = catalogLoader.loadPatternOverlay(locale.code());
        Map<String, Object> challengeOverlay = challengeLoader.loadOverlay(locale.code());
        Map<String, Object> ui = catalogLoader.loadUiBundle(locale.code());

        Map<PatternId, PatternMeta> patterns = new EnumMap<>(PatternId.class);
        for (CourseCatalogLoader.PatternDocument doc : baseCatalog.patterns()) {
            patterns.put(doc.id(), doc.toMeta(cast(patternOverlay.get(doc.id().name()))));
        }

        List<ChallengeMeta> challenges = baseChallenges.stream()
                .map(item -> item.toMeta(cast(challengeOverlay.get(item.id()))))
                .toList();

        String description = ui.get("courseDescription") != null
                ? String.valueOf(ui.get("courseDescription"))
                : baseCatalog.description();

        Map<String, String> weekTitles = new LinkedHashMap<>();
        Object weeks = ui.get("weeks");
        if (weeks instanceof Map<?, ?> map) {
            map.forEach((k, v) -> weekTitles.put(String.valueOf(k), String.valueOf(v)));
        }

        return new LocalizedBundle(description, weekTitles, patterns, challenges, ui);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> cast(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }

    private record LocalizedBundle(
            String courseDescription,
            Map<String, String> weekTitles,
            Map<PatternId, PatternMeta> patterns,
            List<ChallengeMeta> challenges,
            Map<String, Object> ui
    ) {
        String weekTitle(int week) {
            return weekTitles.getOrDefault(String.valueOf(week), "Week " + week);
        }
    }
}
