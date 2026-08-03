package com.algoprep.sync;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sync")
public class ProgressSyncController {
    private final ProgressSyncStore store;

    public ProgressSyncController(ProgressSyncStore store) {
        this.store = store;
    }

    @PostMapping("/keys")
    public Map<String, String> createKey() {
        String key = UUID.randomUUID().toString().replace("-", "");
        store.put(key, Map.of("version", 1, "progress", Map.of(), "meta", Map.of()));
        return Map.of("syncKey", key);
    }

    @GetMapping("/{syncKey}")
    public Map<String, Object> pull(@PathVariable String syncKey) {
        return store.get(syncKey).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown sync key"));
    }

    @PutMapping("/{syncKey}")
    public Map<String, Object> push(@PathVariable String syncKey, @Valid @RequestBody SyncPayload payload) {
        if (!syncKey.matches("[a-f0-9]{32}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid sync key");
        }
        Map<String, Object> body = Map.of(
                "version", payload.version() == null ? 1 : payload.version(),
                "progress", payload.progress() == null ? Map.of() : payload.progress(),
                "meta", payload.meta() == null ? Map.of() : payload.meta(),
                "updatedAt", System.currentTimeMillis()
        );
        store.put(syncKey, body);
        return body;
    }

    public record SyncPayload(
            Integer version,
            Map<String, Object> progress,
            Map<String, Object> meta,
            @Size(max = 64) String clientId
    ) {
    }
}
