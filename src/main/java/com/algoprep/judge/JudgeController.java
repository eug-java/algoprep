package com.algoprep.judge;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/judge")
public class JudgeController {
    private final JudgeService judgeService;

    public JudgeController(JudgeService judgeService) {
        this.judgeService = judgeService;
    }

    @PostMapping
    public JudgeResult judge(@Valid @RequestBody JudgeRequest request) {
        return judgeService.judge(request);
    }

    @GetMapping("/template/{patternId}/{problemId}")
    public Map<String, String> template(@PathVariable String patternId, @PathVariable String problemId) {
        return judgeService.template(patternId, problemId);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> invalidRequest(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", exception.getMessage() == null ? "Invalid judge request" : exception.getMessage()));
    }
}
