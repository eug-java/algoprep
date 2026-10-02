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
        Path file = fileFor(key);
        if (file == null) {
            throw new IllegalArgumentException("Invalid sync key");
        }
        memory.put(key, payload);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), payload);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to persist sync payload", exception);
        }
    }

    private Path fileFor(String key) {
        if (key == null || !key.matches("[a-f0-9]{32}")) return null;
        Path file = directory.resolve(key + ".json").normalize();
        if (!file.startsWith(directory)) return null;
        return file;
    }

    public Optional<Map<String, Object>> get(String key) {
        Path file = fileFor(key);
        if (file == null) return Optional.empty();
        Map<String, Object> cached = memory.get(key);
        if (cached != null) return Optional.of(cached);
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
