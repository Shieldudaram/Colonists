package com.shieldudaram.colonists;

import com.shieldudaram.colonists.commands.ColonyCommandRouter;
import com.shieldudaram.colonists.commands.CommandResult;
import com.shieldudaram.colonists.content.ColonistsConfig;
import com.shieldudaram.colonists.content.ConfigLoader;
import com.shieldudaram.colonists.content.ContentPackValidator;
import com.shieldudaram.colonists.sim.ColonyCallbacks;
import com.shieldudaram.colonists.sim.ColonySimulationEngine;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class ColonistsRuntime {
    private static final List<String> DEFAULT_CONTENT_RESOURCES = List.of(
            "content/colonists/config.json",
            "content/colonists/events/bandit_assault.json",
            "content/colonists/hotspots/default_hotspots.json",
            "content/colonists/policies/default_policies.json",
            "content/colonists/raid_factions/bandits.json",
            "content/colonists/recipes/basic_recipes.json"
    );

    private final ColonySimulationEngine engine;
    private final ColonyCommandRouter commandRouter;

    public ColonistsRuntime(Path runtimeRoot) {
        Path root = runtimeRoot == null ? Path.of(".") : runtimeRoot;
        Path logsDir = root.resolve("logs/colonists");
        Path saveDir = root.resolve("saves/colonists");
        Path configPath = root.resolve("config/colonists-config.json");
        Path contentRoot = root.resolve("content/colonists");
        ColonistsConfig config = bootstrapConfig(configPath);
        boolean autosaveEnabled = (config.save == null) || config.save.autosaveEnabled;

        this.engine = new ColonySimulationEngine(logsDir, saveDir, new ColonyCallbacks() {
        }, autosaveEnabled);
        this.commandRouter = new ColonyCommandRouter(engine);

        bootstrapContent(contentRoot);
        validateContent(contentRoot);
    }

    public ColonySimulationEngine engine() {
        return engine;
    }

    public CommandResult handleCommand(String rawCommand) {
        return commandRouter.execute(rawCommand);
    }

    public void tick() {
        engine.tick();
    }

    private ColonistsConfig bootstrapConfig(Path configPath) {
        ConfigLoader loader = new ConfigLoader();
        try {
            loader.writeDefault(configPath);
            ColonistsConfig config = loader.load(configPath);
            if (config == null) {
                return new ColonistsConfig();
            }
            if (config.save == null) {
                config.save = new ColonistsConfig.Save();
            }
            return config;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to initialize config", exception);
        }
    }

    private void bootstrapContent(Path contentRoot) {
        for (String resourcePath : DEFAULT_CONTENT_RESOURCES) {
            Path destination = resolveContentDestination(contentRoot, resourcePath);
            if (Files.exists(destination)) {
                continue;
            }
            copyBundledResource(resourcePath, destination);
        }
    }

    private void validateContent(Path contentRoot) {
        new ContentPackValidator().validateNoDuplicateIds(contentRoot);
    }

    private Path resolveContentDestination(Path contentRoot, String resourcePath) {
        String prefix = "content/colonists/";
        if (!resourcePath.startsWith(prefix)) {
            throw new IllegalArgumentException("Unexpected content resource path: " + resourcePath);
        }
        String relativePath = resourcePath.substring(prefix.length());
        return contentRoot.resolve(relativePath);
    }

    private void copyBundledResource(String resourcePath, Path destination) {
        try (InputStream stream = ColonistsRuntime.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (stream == null) {
                throw new IllegalStateException("Missing bundled resource: " + resourcePath);
            }
            Files.createDirectories(destination.getParent());
            Files.copy(stream, destination);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to seed bundled resource: " + resourcePath, exception);
        }
    }
}
