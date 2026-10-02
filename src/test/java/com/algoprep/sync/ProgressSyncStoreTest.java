package com.algoprep.sync;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProgressSyncStoreTest {

    @TempDir
    Path directory;

    @Test
    void rejectsKeysThatLeaveTheSyncDirectory() throws Exception {
        ProgressSyncStore store = new ProgressSyncStore(directory.toString());
        assertTrue(store.get("../secret").isEmpty());
        assertTrue(store.get("not-a-key").isEmpty());
        assertThrows(IllegalArgumentException.class, () -> store.put("../secret", Map.of("version", 1)));
    }
}