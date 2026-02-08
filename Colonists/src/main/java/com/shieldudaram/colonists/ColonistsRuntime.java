package com.shieldudaram.colonists;

import com.shieldudaram.colonists.commands.ColonyCommandRouter;
import com.shieldudaram.colonists.commands.CommandResult;
import com.shieldudaram.colonists.content.ConfigLoader;
import com.shieldudaram.colonists.content.ContentPackValidator;
import com.shieldudaram.colonists.sim.ColonyCallbacks;
import com.shieldudaram.colonists.sim.ColonySimulationEngine;

import java.io.IOException;
import java.nio.file.Path;

public final class ColonistsRuntime {
    private final ColonySimulationEngine engine;
    private final ColonyCommandRouter commandRouter;

    public ColonistsRuntime(Path workspaceRoot) {
        Path logsDir = workspaceRoot.resolve("logs/colonists");
        Path saveDir = workspaceRoot.resolve("saves/colonists");
        this.engine = new ColonySimulationEngine(logsDir, saveDir, new ColonyCallbacks() {
        });
        this.commandRouter = new ColonyCommandRouter(engine);

        Path configPath = workspaceRoot.resolve("src/main/resources/content/colonists/config.json");
        ConfigLoader loader = new ConfigLoader();
        try {
            loader.writeDefault(configPath);
            loader.load(configPath);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to initialize config", exception);
        }

        Path contentRoot = workspaceRoot.resolve("src/main/resources/content/colonists");
        new ContentPackValidator().validateNoDuplicateIds(contentRoot);
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
}
