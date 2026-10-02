package com.algoprep.course.api;

import com.algoprep.course.i18n.CourseLocale;
import com.algoprep.course.model.ChallengeMeta;
import com.algoprep.course.model.CourseOverview;
import com.algoprep.course.model.PatternId;
import com.algoprep.course.model.PatternMeta;
import com.algoprep.course.model.ProblemMeta;
import com.algoprep.course.model.SourceSnippet;
import com.algoprep.course.service.CourseService;
import com.algoprep.course.service.PatternQuizService;
import com.algoprep.course.service.SourceService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/course")
public class CourseController {

    private final CourseService courseService;
    private final SourceService sourceService;
    private final PatternQuizService quizService;
    private final LocaleResolverSupport localeResolver;

    public CourseController(
            CourseService courseService,
            SourceService sourceService,
            PatternQuizService quizService,
            LocaleResolverSupport localeResolver) {
        this.courseService = courseService;
        this.sourceService = sourceService;
        this.quizService = quizService;
        this.localeResolver = localeResolver;
    }

    @GetMapping
    public CourseOverview overview(HttpServletRequest request, @RequestParam(required = false) String lang) {
        return courseService.overview(locale(request, lang));
    }

    @GetMapping("/ui")
    public Map<String, Object> ui(HttpServletRequest request, @RequestParam(required = false) String lang) {
        CourseLocale locale = locale(request, lang);
        Map<String, Object> bundle = new LinkedHashMap<>(courseService.uiBundle(locale));
        bundle.put("locale", locale.code());
        bundle.put("supportedLocales", List.of("en", "ru", "es"));
        return bundle;
    }

    @GetMapping("/patterns")
    public List<PatternMeta> patterns(HttpServletRequest request, @RequestParam(required = false) String lang) {
        return courseService.listPatterns(locale(request, lang));
    }

    @GetMapping("/patterns/{patternId}")
    public PatternMeta pattern(
            HttpServletRequest request,
            @PathVariable PatternId patternId,
            @RequestParam(required = false) String lang) {
        return courseService.getPattern(locale(request, lang), patternId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pattern not found"));
    }

    @GetMapping("/patterns/{patternId}/problems/{problemId}")
    public ProblemMeta problem(
            HttpServletRequest request,
            @PathVariable PatternId patternId,
            @PathVariable String problemId,
            @RequestParam(required = false) String lang) {
        return courseService.getProblem(locale(request, lang), patternId, problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));
    }

    @GetMapping("/walkthroughs/{problemId}")
    public Map<String, String> walkthrough(
            HttpServletRequest request,
            @PathVariable String problemId,
            @RequestParam(required = false) String lang) {
        String ascii = courseService.getWalkthrough(locale(request, lang), problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Walkthrough not found"));
        Map<String, String> body = new LinkedHashMap<>();
        body.put("problemId", problemId);
        body.put("walkthroughAscii", ascii);
        return body;
    }

    @GetMapping("/patterns/{patternId}/problems/{problemId}/source")
    public SourceSnippet problemSource(
            @PathVariable PatternId patternId,
            @PathVariable String problemId) {
        // source is Java code — language-independent; use EN metadata for lookup
        ProblemMeta problem = courseService.getProblem(CourseLocale.EN, patternId, problemId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found"));
        return sourceService.loadByClassName(problem.className())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source not found"));
    }

    @GetMapping("/challenges")
    public List<Map<String, Object>> challenges(
            HttpServletRequest request, @RequestParam(required = false) String lang) {
        return courseService.listChallenges(locale(request, lang)).stream()
                .map(this::publicChallengeView)
                .toList();
    }

    @GetMapping("/challenges/{challengeId}")
    public Map<String, Object> challenge(
            HttpServletRequest request,
            @PathVariable String challengeId,
            @RequestParam(required = false) String lang) {
        ChallengeMeta meta = courseService.getChallenge(locale(request, lang), challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));
        return publicChallengeView(meta);
    }

    @GetMapping("/challenges/{challengeId}/reveal")
    public ChallengeMeta reveal(
            HttpServletRequest request,
            @PathVariable String challengeId,
            @RequestParam(required = false) String lang) {
        return courseService.getChallenge(locale(request, lang), challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));
    }

    @GetMapping("/challenges/{challengeId}/source")
    public SourceSnippet challengeSource(@PathVariable String challengeId) {
        ChallengeMeta meta = courseService.getChallenge(CourseLocale.EN, challengeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge not found"));
        return sourceService.loadByClassName(meta.className())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source not found"));
    }

    @GetMapping("/quiz")
    public Map<String, Object> quiz(HttpServletRequest request, @RequestParam(required = false) String lang) {
        CourseLocale locale = locale(request, lang);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("count", quizService.size());
        body.put("questions", quizService.publicQuestions(locale));
        return body;
    }

    @PostMapping("/quiz/{questionId}/check")
    public Map<String, Object> checkQuiz(
            HttpServletRequest request,
            @PathVariable String questionId,
            @Valid @RequestBody QuizAnswerRequest answer,
            @RequestParam(required = false) String lang) {
        return quizService.check(locale(request, lang), questionId, answer.selectedPattern())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quiz question not found"));
    }

    private CourseLocale locale(HttpServletRequest request, String lang) {
        return localeResolver.resolve(request, lang);
    }

    private Map<String, Object> publicChallengeView(ChallengeMeta meta) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", meta.id());
        view.put("title", meta.title());
        view.put("difficulty", meta.difficulty());
        view.put("prompt", meta.prompt());
        view.put("className", meta.className());
        view.put("hints", meta.hints());
        return view;
    }
}
