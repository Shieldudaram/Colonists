package com.shieldudaram.colonists.content;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class ContentPackValidator {
    private final ObjectMapper mapper = new ObjectMapper();

    public void validateNoDuplicateIds(Path baseDir) {
        try {
            checkCategory(baseDir.resolve("hotspots"), "hotspots");
            checkCategory(baseDir.resolve("recipes"), "recipes");
            checkCategory(baseDir.resolve("events"), "events");
            checkCategory(baseDir.resolve("raid_factions"), "raid_factions");
            checkCategory(baseDir.resolve("policies"), "policies");
        } catch (IOException exception) {
            throw new IllegalStateException("Failed reading content packs", exception);
        }
    }

    private void checkCategory(Path categoryPath, String category) throws IOException {
        if (!Files.exists(categoryPath)) {
            return;
        }
        Map<String, Path> seen = new HashMap<>();
        try (Stream<Path> stream = Files.walk(categoryPath)) {
            stream
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .forEach(path -> collectIds(category, path, seen));
        }
    }

    private void collectIds(String category, Path file, Map<String, Path> seen) {
        try {
            JsonNode node = mapper.readTree(file.toFile());
            if (node.isArray()) {
                for (JsonNode item : node) {
                    capture(category, file, seen, item.path("id").asText(null));
                }
            } else {
                capture(category, file, seen, node.path("id").asText(null));
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed parsing content file: " + file, exception);
        }
    }

    private void capture(String category, Path file, Map<String, Path> seen, String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        Path previous = seen.putIfAbsent(id, file);
        if (previous != null) {
            throw new IllegalStateException(
                    "Duplicate content id in " + category + ": " + id
                            + " first=" + previous
                            + " duplicate=" + file
            );
        }
    }
}
