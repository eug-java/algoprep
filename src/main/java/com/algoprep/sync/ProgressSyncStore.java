package com.algoprep.sync;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProgressSyncStore {
    private final ConcurrentHashMap<String, Map<String, Object>> memory = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path directory;

    public ProgressSyncStore(@Value("${algoprep.sync.dir:./data/sync}") String dir) throws IOException {
        this.directory = Path.of(dir).toAbsolutePath().normalize();
        Files.createDirectories(directory);
    }

    public void put(String key, Map<String, Object> payload) {
        memory.put(key, payload);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(directory.resolve(key + ".json").toFile(), payload);
        } catch (IOException ignored) {
            // in-memory remains source of truth for this process
        }
    }

    public Optional<Map<String, Object>> get(String key) {
        Map<String, Object> cached = memory.get(key);
        if (cached != null) return Optional.of(cached);
        Path file = directory.resolve(key + ".json");
        if (!Files.isRegularFile(file)) return Optional.empty();
        try {
            Map<String, Object> loaded = mapper.readValue(file.toFile(), new TypeReference<>() {});
            memory.put(key, loaded);
            return Optional.of(loaded);
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
