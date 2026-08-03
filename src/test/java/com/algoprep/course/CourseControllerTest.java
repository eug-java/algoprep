package com.algoprep.course;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void overviewExposesPatternsAndProblems() throws Exception {
        mockMvc.perform(get("/api/v1/course").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patternCount").value(greaterThanOrEqualTo(28)))
                .andExpect(jsonPath("$.problemCount").value(greaterThanOrEqualTo(100)))
                .andExpect(jsonPath("$.easyCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.mediumCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.hardCount").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.patterns[0].easyCount").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.patterns[0].mediumCount").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.patterns[0].hardCount").value(greaterThanOrEqualTo(0)))
                .andExpect(jsonPath("$.weeks", hasSize(0)))
                .andExpect(jsonPath("$.language").value("en"));
    }

    @Test
    void russianLocaleTranslatesPatternTitles() throws Exception {
        mockMvc.perform(get("/api/v1/course/patterns/TWO_POINTERS").param("lang", "ru"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Два указателя"))
                .andExpect(jsonPath("$.problems", hasSize(greaterThanOrEqualTo(5))))
                .andExpect(jsonPath("$.walkthrough").isNotEmpty());
    }

    @Test
    void spanishLocaleTranslatesPatternTitles() throws Exception {
        mockMvc.perform(get("/api/v1/course/patterns/TWO_POINTERS").param("lang", "es"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Dos punteros"));
    }

    @Test
    void uiBundleIncludesCheatsheet() throws Exception {
        mockMvc.perform(get("/api/v1/course/ui").param("lang", "ru"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locale").value("ru"))
                .andExpect(jsonPath("$.cheatsheet", hasSize(greaterThanOrEqualTo(12))));
    }

    @Test
    void listsAllPatterns() throws Exception {
        mockMvc.perform(get("/api/v1/course/patterns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(28))));
    }

    @Test
    void returnsProblemSource() throws Exception {
        mockMvc.perform(get("/api/v1/course/patterns/TWO_POINTERS/problems/container-with-most-water/source"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.language").value("java"))
                .andExpect(jsonPath("$.source").isNotEmpty());
    }

    @Test
    void listsChallengesWithoutRevealingPattern() throws Exception {
        mockMvc.perform(get("/api/v1/course/challenges").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(8))))
                .andExpect(jsonPath("$[0].hiddenPattern").doesNotExist());
    }

    @Test
    void revealsChallengePattern() throws Exception {
        mockMvc.perform(get("/api/v1/course/challenges/trap-rain-water/reveal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hiddenPattern").value("TWO_POINTERS"));
    }

    @Test
    void quizReturnsQuestionsWithoutAnswers() throws Exception {
        mockMvc.perform(get("/api/v1/course/quiz").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questions", hasSize(greaterThanOrEqualTo(20))))
                .andExpect(jsonPath("$.questions[0].correctPattern").doesNotExist())
                .andExpect(jsonPath("$.questions[0].prompt").isNotEmpty())
                .andExpect(jsonPath("$.questions[0].options", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    void quizCheckAnswers() throws Exception {
        mockMvc.perform(post("/api/v1/course/quiz/q-cycle-list/check")
                        .param("lang", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"selectedPattern\":\"FAST_SLOW_POINTERS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.correctPattern").value("FAST_SLOW_POINTERS"));
    }

    @Test
    void servesSpaShell() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
