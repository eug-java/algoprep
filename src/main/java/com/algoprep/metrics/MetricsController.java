package com.algoprep.metrics;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/metrics")
public class MetricsController {
    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @PostMapping("/events")
    public Map<String, Object> event(@Valid @RequestBody MetricEvent event) {
        metricsService.record(event);
        return Map.of("ok", true);
    }

    @GetMapping("/summary")
    public Map<String, Object> summary() {
        return metricsService.summary();
    }

    public record MetricEvent(
            @NotBlank @Size(max = 64) String type,
            @Size(max = 64) String patternId,
            @Size(max = 64) String problemId,
            Long durationMs,
            Map<String, Object> props
    ) {
    }
}
