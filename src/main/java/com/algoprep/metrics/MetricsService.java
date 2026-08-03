package com.algoprep.metrics;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class MetricsService {
    private final ConcurrentHashMap<String, AtomicLong> counts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> abandonByPattern = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> openByPattern = new ConcurrentHashMap<>();
    private final AtomicLong totalEvents = new AtomicLong();

    public void record(MetricsController.MetricEvent event) {
        totalEvents.incrementAndGet();
        counts.computeIfAbsent(event.type(), k -> new AtomicLong()).incrementAndGet();
        if (event.patternId() != null && !event.patternId().isBlank()) {
            if ("abandon".equals(event.type()) || "leave".equals(event.type())) {
                abandonByPattern.computeIfAbsent(event.patternId(), k -> new AtomicLong()).incrementAndGet();
            }
            if ("open".equals(event.type())) {
                openByPattern.computeIfAbsent(event.patternId(), k -> new AtomicLong()).incrementAndGet();
            }
        }
    }

    public Map<String, Object> summary() {
        List<Map<String, Object>> abandoned = new ArrayList<>();
        abandonByPattern.forEach((pattern, count) -> abandoned.add(Map.of(
                "patternId", pattern,
                "abandons", count.get(),
                "opens", openByPattern.getOrDefault(pattern, new AtomicLong()).get()
        )));
        abandoned.sort(Comparator.comparingLong((Map<String, Object> m) -> ((Number) m.get("abandons")).longValue()).reversed());
        return Map.of(
                "totalEvents", totalEvents.get(),
                "byType", toMap(counts),
                "topAbandoned", abandoned.stream().limit(20).toList()
        );
    }

    private Map<String, Long> toMap(ConcurrentHashMap<String, AtomicLong> source) {
        ConcurrentHashMap<String, Long> out = new ConcurrentHashMap<>();
        source.forEach((k, v) -> out.put(k, v.get()));
        return out;
    }
}
