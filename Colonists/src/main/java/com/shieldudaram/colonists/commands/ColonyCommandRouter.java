package com.shieldudaram.colonists.commands;

import com.shieldudaram.colonists.model.BlueprintId;
import com.shieldudaram.colonists.model.HotspotFamily;
import com.shieldudaram.colonists.model.PolicyId;
import com.shieldudaram.colonists.model.TaskType;
import com.shieldudaram.colonists.model.ZoneType;
import com.shieldudaram.colonists.sim.ColonySimulationEngine;
import com.shieldudaram.colonists.telemetry.TelemetryMode;

import java.util.Locale;

public final class ColonyCommandRouter {
    private final ColonySimulationEngine engine;
    private Integer mark1x;
    private Integer mark1z;
    private Integer mark2x;
    private Integer mark2z;

    public ColonyCommandRouter(ColonySimulationEngine engine) {
        this.engine = engine;
    }

    public CommandResult execute(String rawCommand) {
        try {
            String[] parts = rawCommand.trim().split("\\s+");
            if (parts.length == 0 || parts[0].isBlank()) {
                return CommandResult.error("Empty command");
            }

            if (!"/colony".equalsIgnoreCase(parts[0])) {
                return CommandResult.error("Unsupported namespace. Use /colony");
            }

            if (parts.length == 1) {
                return CommandResult.ok(engine.status(TelemetryMode.BRIEF));
            }

            String sub = parts[1].toLowerCase(Locale.ROOT);
            return switch (sub) {
                case "status" -> CommandResult.ok(engine.status(parseModeArg(parts, 2)));
                case "tasks" -> CommandResult.ok(engine.status(parseModeArg(parts, 2)));
                case "hotspots" -> CommandResult.ok(engine.status(parseModeArg(parts, 2)));
                case "raid" -> CommandResult.ok(engine.status(parseModeArg(parts, 2)));
                case "pause" -> handlePause();
                case "resume" -> handleResume();
                case "save" -> handleSave();
                case "priority" -> handlePriority(parts);
                case "policy" -> handlePolicy(parts);
                case "build" -> handleBuild(parts);
                case "hotspot" -> handleHotspot(parts);
                case "zone" -> handleZone(parts);
                case "crisis" -> handleCrisis(parts);
                case "telemetry" -> handleTelemetry(parts);
                default -> CommandResult.error("Unknown /colony subcommand: " + sub);
            };
        } catch (Exception exception) {
            return CommandResult.error(exception.getMessage());
        }
    }

    private CommandResult handlePause() {
        engine.setPaused(true);
        return CommandResult.ok("Simulation paused.");
    }

    private CommandResult handleResume() {
        engine.setPaused(false);
        return CommandResult.ok("Simulation resumed.");
    }

    private CommandResult handleSave() {
        engine.saveNow();
        return CommandResult.ok("Save completed.");
    }

    private CommandResult handlePriority(String[] parts) {
        if (parts.length < 5 || !"set".equalsIgnoreCase(parts[2])) {
            return CommandResult.error("Usage: /colony priority set <build|farm|gather|haul|defend|repair> <0.50-2.00>");
        }
        TaskType type = engine.parseTaskType(parts[3]);
        double value = Double.parseDouble(parts[4]);
        if (value < 0.5 || value > 2.0) {
            return CommandResult.error("Priority value must be between 0.50 and 2.00");
        }
        engine.setPriority(type, value);
        return CommandResult.ok("Priority updated: " + type.name() + "=" + value);
    }

    private CommandResult handlePolicy(String[] parts) {
        if (parts.length < 4 || !"set".equalsIgnoreCase(parts[2])) {
            return CommandResult.error("Usage: /colony policy set <Fortify|HarvestRush|Recovery>");
        }
        PolicyId policyId = engine.parsePolicyId(parts[3]);
        engine.applyPolicy(policyId);
        return CommandResult.ok("Policy updated: " + policyId.name());
    }

    private CommandResult handleBuild(String[] parts) {
        if (parts.length < 7 || !"place".equalsIgnoreCase(parts[2])) {
            return CommandResult.error("Usage: /colony build place <TownCore|House|Stockpile|Watchtower|TrapPost|FarmShed|Workshop|Infirmary> <x> <z> <rotation>");
        }
        BlueprintId blueprintId = engine.parseBlueprintId(parts[3]);
        int x = Integer.parseInt(parts[4]);
        int z = Integer.parseInt(parts[5]);
        int rotation = Integer.parseInt(parts[6]);
        var structure = engine.placeBlueprint(blueprintId, x, z, rotation);
        return CommandResult.ok("Blueprint queued: " + structure.id());
    }

    private CommandResult handleHotspot(String[] parts) {
        if (parts.length < 3) {
            return CommandResult.error("Usage: /colony hotspot <place|upgrade> ...");
        }
        String action = parts[2].toLowerCase(Locale.ROOT);
        if ("place".equals(action)) {
            if (parts.length < 6) {
                return CommandResult.error("Usage: /colony hotspot place <family> <x> <z>");
            }
            HotspotFamily family = engine.parseFamily(parts[3]);
            int x = Integer.parseInt(parts[4]);
            int z = Integer.parseInt(parts[5]);
            var hotspot = engine.placeHotspot(family, x, z);
            return CommandResult.ok("Hotspot placed: " + hotspot.id());
        }
        if ("upgrade".equals(action)) {
            if (parts.length < 4) {
                return CommandResult.error("Usage: /colony hotspot upgrade <hotspotId>");
            }
            var hotspot = engine.upgradeHotspot(parts[3]);
            return CommandResult.ok("Hotspot upgraded: " + hotspot.id() + " tier=" + hotspot.tier());
        }
        return CommandResult.error("Unknown hotspot action: " + action);
    }

    private CommandResult handleZone(String[] parts) {
        if (parts.length < 3) {
            return CommandResult.error("Usage: /colony zone <mark1|mark2|create|clear> ...");
        }
        String action = parts[2].toLowerCase(Locale.ROOT);
        return switch (action) {
            case "mark1" -> setMark(parts, true);
            case "mark2" -> setMark(parts, false);
            case "create" -> createZone(parts);
            case "clear" -> clearZone(parts);
            default -> CommandResult.error("Unknown zone action: " + action);
        };
    }

    private CommandResult setMark(String[] parts, boolean first) {
        if (parts.length < 5) {
            return CommandResult.error("Usage: /colony zone mark1 <x> <z> OR /colony zone mark2 <x> <z>");
        }
        int x = Integer.parseInt(parts[3]);
        int z = Integer.parseInt(parts[4]);
        if (first) {
            mark1x = x;
            mark1z = z;
            return CommandResult.ok("mark1 set");
        }
        mark2x = x;
        mark2z = z;
        return CommandResult.ok("mark2 set");
    }

    private CommandResult createZone(String[] parts) {
        if (parts.length < 4) {
            return CommandResult.error("Usage: /colony zone create <Home|Farm|Defense|Hotspot|Storage>");
        }
        if (mark1x == null || mark2x == null || mark1z == null || mark2z == null) {
            return CommandResult.error("Set mark1 and mark2 first");
        }
        ZoneType zoneType = engine.parseZoneType(parts[3]);
        var zone = engine.createZone(zoneType, mark1x, mark1z, mark2x, mark2z);
        return CommandResult.ok("Zone created: " + zone.id() + " type=" + zone.type());
    }

    private CommandResult clearZone(String[] parts) {
        if (parts.length < 4) {
            return CommandResult.error("Usage: /colony zone clear <zoneId>");
        }
        boolean removed = engine.clearZone(parts[3]);
        if (!removed) {
            return CommandResult.error("Zone not found: " + parts[3]);
        }
        return CommandResult.ok("Zone cleared: " + parts[3]);
    }

    private CommandResult handleCrisis(String[] parts) {
        if (parts.length < 4 || !"start".equalsIgnoreCase(parts[2])) {
            return CommandResult.error("Usage: /colony crisis start bandit_assault");
        }
        if (!"bandit_assault".equalsIgnoreCase(parts[3])) {
            return CommandResult.error("Only bandit_assault is supported in v1");
        }
        engine.createTask(TaskType.DEFEND, "crisis-bandit-assault", 2.0, true);
        return CommandResult.ok("Bandit assault crisis started.");
    }

    private CommandResult handleTelemetry(String[] parts) {
        if (parts.length < 3) {
            return CommandResult.error("Usage: /colony telemetry <brief|full|off>");
        }
        TelemetryMode mode = parseMode(parts[2]);
        engine.setTelemetryMode(mode);
        return CommandResult.ok("Telemetry mode set to " + mode.name());
    }

    private TelemetryMode parseModeArg(String[] parts, int index) {
        if (parts.length <= index) {
            return TelemetryMode.BRIEF;
        }
        return parseMode(parts[index]);
    }

    private TelemetryMode parseMode(String raw) {
        String normalized = raw.trim().replace("--", "").toUpperCase(Locale.ROOT);
        return TelemetryMode.valueOf(normalized);
    }
}
