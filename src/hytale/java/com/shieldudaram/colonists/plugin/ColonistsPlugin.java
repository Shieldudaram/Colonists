package com.shieldudaram.colonists.plugin;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.shieldudaram.colonists.ColonistsRuntime;

import javax.annotation.Nonnull;

public final class ColonistsPlugin extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private ColonistsRuntime runtime;

    public ColonistsPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Loaded %s v%s", this.getName(), this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up ColonistsPlugin...");
        try {
            this.runtime = new ColonistsRuntime(this.getDataDirectory());
            this.getCommandRegistry().registerCommand(new ColonistsCommand(this.runtime));
            this.getEntityStoreRegistry().registerSystem(new ColonistsTickSystem(this.runtime));
            LOGGER.atInfo().log("[Colonists] Plugin setup complete.");
        } catch (RuntimeException exception) {
            LOGGER.atWarning().withCause(exception).log("[Colonists] Failed to initialize plugin.");
            throw exception;
        }
    }
}
