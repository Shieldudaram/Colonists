package com.shieldudaram.colonists.content;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigLoader {
    private final ObjectMapper mapper;

    public ConfigLoader() {
        this.mapper = JsonMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .build();
    }

    public ColonistsConfig load(Path configPath) throws IOException {
        if (!Files.exists(configPath)) {
            throw new IOException("Config file not found: " + configPath);
        }
        return mapper.readValue(configPath.toFile(), ColonistsConfig.class);
    }

    public void writeDefault(Path configPath) throws IOException {
        Files.createDirectories(configPath.getParent());
        if (!Files.exists(configPath)) {
            mapper.writeValue(configPath.toFile(), new ColonistsConfig());
        }
    }
}
