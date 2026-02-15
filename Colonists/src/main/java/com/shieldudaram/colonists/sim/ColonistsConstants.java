package com.shieldudaram.colonists.sim;

import com.shieldudaram.colonists.model.PolicyId;
import com.shieldudaram.colonists.model.PolicyWeights;
import com.shieldudaram.colonists.model.TaskType;

import java.util.EnumMap;
import java.util.Map;

public final class ColonistsConstants {
    public static final int SCHEMA_VERSION = 1;
    public static final int TICK_HZ = 5;
    public static final int DAY_LENGTH_MINUTES = 24;
    public static final int AUTOSAVE_SECONDS = 300;
    public static final int AUTOSAVE_ROTATIONS = 5;
    public static final int MAX_CITIZENS = 5;
    public static final int MAX_HOTSPOTS_PER_FAMILY = 1;
    public static final int MAX_ACTIVE_RAID_ENEMIES = 1;
    public static final int HOTSPOT_SPACING_METERS = 12;
    public static final int HOTSPOT_PER_ZONE_CAP = 2;
    public static final int TASK_PREEMPT_LOCK_SECONDS = 10;
    public static final int TASK_PATH_RETRIES = 2;
    public static final int TASK_QUARANTINE_SECONDS = 60;

    public static final int RAID_GRACE_SECONDS = 30 * 60;
    public static final int RAID_BASE_INTERVAL_SECONDS = 15 * 60;
    public static final int RAID_TRIGGER_COOLDOWN_SECONDS = 8 * 60;

    public static final int STARTING_CITIZENS = 2;

    public static final int[] XP_THRESHOLDS = {
            100, 250, 450, 700, 1000, 1350, 1750, 2200, 2700
    };

    public static final int XP_BUILDER = 8;
    public static final int XP_FARMER = 6;
    public static final int XP_GATHERER = 5;
    public static final int XP_HAULER = 4;
    public static final int XP_GUARD_TAKEDOWN = 10;
    public static final int XP_GUARD_PATROL = 4;

    private ColonistsConstants() {
    }

    public record HotspotTierProfile(
            int capacity,
            int resetSeconds,
            int baseYield,
            double degradationStep,
            double degradationFloor,
            int minQuality,
            int maxQuality
    ) {
    }

    public static HotspotTierProfile tierProfile(int tier) {
        return switch (tier) {
            case 1 -> new HotspotTierProfile(120, 600, 6, 0.015, 0.40, 1, 2);
            case 2 -> new HotspotTierProfile(180, 480, 8, 0.010, 0.40, 2, 3);
            case 3 -> new HotspotTierProfile(260, 360, 10, 0.008, 0.40, 3, 5);
            default -> throw new IllegalArgumentException("Unsupported tier: " + tier);
        };
    }

    public static PolicyWeights policyWeights(PolicyId policyId) {
        EnumMap<TaskType, Double> weights = new EnumMap<>(TaskType.class);
        switch (policyId) {
            case FORTIFY -> {
                weights.put(TaskType.BUILD, 0.8);
                weights.put(TaskType.FARM, 0.9);
                weights.put(TaskType.GATHER, 0.8);
                weights.put(TaskType.HAUL, 1.0);
                weights.put(TaskType.DEFEND, 1.5);
                weights.put(TaskType.REPAIR, 1.5);
                weights.put(TaskType.EMERGENCY, 2.0);
            }
            case HARVEST_RUSH -> {
                weights.put(TaskType.BUILD, 0.9);
                weights.put(TaskType.FARM, 1.5);
                weights.put(TaskType.GATHER, 1.5);
                weights.put(TaskType.HAUL, 1.4);
                weights.put(TaskType.DEFEND, 0.8);
                weights.put(TaskType.REPAIR, 0.8);
                weights.put(TaskType.EMERGENCY, 2.0);
            }
            case RECOVERY -> {
                weights.put(TaskType.BUILD, 0.9);
                weights.put(TaskType.FARM, 1.4);
                weights.put(TaskType.GATHER, 1.1);
                weights.put(TaskType.HAUL, 1.3);
                weights.put(TaskType.DEFEND, 0.8);
                weights.put(TaskType.REPAIR, 1.2);
                weights.put(TaskType.EMERGENCY, 2.0);
            }
        }
        return new PolicyWeights(weights);
    }

    public static Map<String, Integer> startupStockpile() {
        return Map.of(
                "wood", 100,
                "stone", 70,
                "fiber", 30,
                "food", 40,
                "hide", 10,
                "crystal", 0
        );
    }
}
