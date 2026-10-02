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

import java.util.ArrayList;
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
    private final Map<CourseLocale, Map<String, String>> walkthroughs;
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
        this.walkthroughs = new EnumMap<>(CourseLocale.class);
        for (CourseLocale locale : CourseLocale.values()) {
            this.walkthroughs.put(locale, catalogLoader.loadWalkthroughs(locale.code()));
        }
    }

    public CourseOverview overview(CourseLocale locale) {
        LocalizedBundle bundle = bundle(locale);
        List<CourseOverview.PatternSummary> summaries = bundle.patterns().values().stream()
                .sorted(Comparator.comparingInt(PatternMeta::order))
                .map(p -> {
                    int[] byDiff = difficultyCounts(p.problems());
                    return new CourseOverview.PatternSummary(
                            p.id(),
                            p.order(),
                            p.week(),
                            p.title(),
                            p.problems().size(),
                            byDiff[0],
                            byDiff[1],
                            byDiff[2],
                            p.subtitle());
                })
                .toList();

        int catalogProblems = summaries.stream().mapToInt(CourseOverview.PatternSummary::problemCount).sum();
        int problemCount = catalogProblems + bundle.challenges().size();
        int easyCount = summaries.stream().mapToInt(CourseOverview.PatternSummary::easyCount).sum();
        int mediumCount = summaries.stream().mapToInt(CourseOverview.PatternSummary::mediumCount).sum();
        int hardCount = summaries.stream().mapToInt(CourseOverview.PatternSummary::hardCount).sum()
                + bundle.challenges().size();

        Map<Integer, List<PatternId>> byWeek = new LinkedHashMap<>();
        for (CourseOverview.PatternSummary summary : summaries) {
            byWeek.computeIfAbsent(summary.week(), ignored -> new ArrayList<>()).add(summary.id());
        }
        List<CourseOverview.WeekSummary> weeks = byWeek.entrySet().stream()
                .map(entry -> new CourseOverview.WeekSummary(
                        entry.getKey(),
                        bundle.weekTitle(entry.getKey()),
                        List.copyOf(entry.getValue())))
                .toList();

        return new CourseOverview(
                properties.title(),
                locale.code(),
                properties.version(),
                bundle.courseDescription(),
                summaries.size(),
                problemCount,
                easyCount,
                mediumCount,
                hardCount,
                weeks,
                summaries
        );
    }

    private static int[] difficultyCounts(List<ProblemMeta> problems) {
        int easy = 0;
        int medium = 0;
        int hard = 0;
        for (ProblemMeta problem : problems) {
            if (problem.difficulty() == null) continue;
            switch (problem.difficulty()) {
                case EASY -> easy++;
                case MEDIUM -> medium++;
                case HARD -> hard++;
            }
        }
        return new int[] {easy, medium, hard};
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
                .map(problem -> attachWalkthrough(locale, problem));
    }

    public Optional<String> getWalkthrough(CourseLocale locale, String problemId) {
        String text = walkthroughText(locale, problemId);
        if (text == null || text.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(text);
    }

    private ProblemMeta attachWalkthrough(CourseLocale locale, ProblemMeta problem) {
        String ascii = walkthroughText(locale, problem.id());
        if (ascii == null || ascii.isBlank()) {
            return problem;
        }
        return problem.withWalkthroughAscii(ascii);
    }

    private String walkthroughText(CourseLocale locale, String problemId) {
        Map<String, String> localized = walkthroughs.getOrDefault(locale, Map.of());
        String text = localized.get(problemId);
        if (text == null || text.isBlank()) {
            text = walkthroughs.getOrDefault(CourseLocale.EN, Map.of()).get(problemId);
        }
        return text;
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
