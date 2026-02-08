package com.shieldudaram.colonists.plugin;

import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.shieldudaram.colonists.ColonistsRuntime;
import com.shieldudaram.colonists.commands.CommandResult;

import javax.annotation.Nonnull;

public final class ColonistsCommand extends CommandBase {
    private static final Message MSG_USAGE = Message.raw(
            "Usage: /colony <status|tasks|hotspots|raid|priority|policy|pause|resume|save|build|hotspot|zone|crisis|telemetry>"
    );

    private final ColonistsRuntime runtime;

    public ColonistsCommand(ColonistsRuntime runtime) {
        super("colony", "Colonists colony simulation commands.");
        this.runtime = runtime;
        this.addAliases("colonists");
        this.setAllowsExtraArguments(true);
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void executeSync(@Nonnull CommandContext ctx) {
        String[] args = splitArgs(ctx.getInputString());
        if (args.length == 0) {
            ctx.sendMessage(MSG_USAGE);
            return;
        }

        CommandResult result = runtime.handleCommand(normalizeRawCommand(args));
        String message = result.message();
        if (message == null || message.isBlank()) {
            message = result.success() ? "OK" : "Command failed.";
        }
        String prefix = result.success() ? "[Colonists] " : "[Colonists] Error: ";
        ctx.sendMessage(Message.raw(prefix + message));
    }

    private static String normalizeRawCommand(String[] args) {
        String[] normalized = args.clone();
        normalized[0] = "colony";
        return "/" + String.join(" ", normalized);
    }

    private static String[] splitArgs(String input) {
        if (input == null) {
            return new String[0];
        }
        String normalized = input.trim();
        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1).trim();
        }
        return normalized.isEmpty() ? new String[0] : normalized.split("\\s+");
    }
}
