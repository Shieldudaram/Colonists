package com.shieldudaram.colonists.content;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentPackValidatorTest {
    @TempDir
    Path tempDir;

    @Test
    void duplicateIdsCauseHardFailure() throws IOException {
        Path hotspots = tempDir.resolve("hotspots");
        Files.createDirectories(hotspots);

        Files.writeString(hotspots.resolve("a.json"), "[{\"id\":\"dup-id\"}]");
        Files.writeString(hotspots.resolve("b.json"), "[{\"id\":\"dup-id\"}]");

        ContentPackValidator validator = new ContentPackValidator();
        assertThrows(IllegalStateException.class, () -> validator.validateNoDuplicateIds(tempDir));
    }
}
