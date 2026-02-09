package com.shieldudaram.colonists.model;

import com.shieldudaram.colonists.sim.ColonistsConstants;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ColonyState {
    private long worldTimeSec;
    private boolean paused;
    private PolicyId activePolicy;
    private PolicyWeights taskWeights;
    private int populationCap;
    private final List<CitizenState> citizens;
    private final List<HotspotState> hotspots;
    private final List<ColonyTask> tasks;
    private final List<ColonyZone> zones;
    private final List<PlacedStructure> structures;
    private final RaidState raidState;
    private final InsuranceState insuranceState;

    private int stockWood;
    private int stockStone;
    private int stockFiber;
    private int stockFood;
    private int stockHide;
    private int stockCrystal;
    private int stockOre;
    private int stockHerbs;

    public ColonyState() {
        this.worldTimeSec = 0L;
        this.paused = false;
        this.activePolicy = PolicyId.RECOVERY;
        this.taskWeights = new PolicyWeights(defaultTaskWeights());
        this.populationCap = 2;
        this.citizens = new ArrayList<>();
        this.hotspots = new ArrayList<>();
        this.tasks = new ArrayList<>();
        this.zones = new ArrayList<>();
        this.structures = new ArrayList<>();
        this.raidState = new RaidState();
        this.insuranceState = new InsuranceState(0);
        this.stockWood = 100;
        this.stockStone = 70;
        this.stockFiber = 30;
        this.stockFood = 40;
        this.stockHide = 10;
        this.stockCrystal = 0;
        this.stockOre = 0;
        this.stockHerbs = 0;
    }

    public long worldTimeSec() {
        return worldTimeSec;
    }

    public void setWorldTimeSec(long worldTimeSec) {
        this.worldTimeSec = Math.max(0L, worldTimeSec);
    }

    public boolean paused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public PolicyId activePolicy() {
        return activePolicy;
    }

    public void setActivePolicy(PolicyId activePolicy) {
        this.activePolicy = Objects.requireNonNull(activePolicy, "activePolicy");
    }

    public PolicyWeights taskWeights() {
        return taskWeights;
    }

    public void setTaskWeights(PolicyWeights taskWeights) {
        this.taskWeights = Objects.requireNonNull(taskWeights, "taskWeights");
    }

    public int populationCap() {
        return populationCap;
    }

    public void setPopulationCap(int populationCap) {
        this.populationCap = Math.max(0, Math.min(ColonistsConstants.MAX_CITIZENS, populationCap));
    }

    public int populationCurrent() {
        return citizens.size();
    }

    public List<CitizenState> citizens() {
        return citizens;
    }

    public List<HotspotState> hotspots() {
        return hotspots;
    }

    public List<ColonyTask> tasks() {
        return tasks;
    }

    public List<ColonyZone> zones() {
        return zones;
    }

    public List<PlacedStructure> structures() {
        return structures;
    }

    public RaidState raidState() {
        return raidState;
    }

    public InsuranceState insuranceState() {
        return insuranceState;
    }

    public int stockWood() {
        return stockWood;
    }

    public int stockStone() {
        return stockStone;
    }

    public int stockFiber() {
        return stockFiber;
    }

    public int stockFood() {
        return stockFood;
    }

    public int stockHide() {
        return stockHide;
    }

    public int stockCrystal() {
        return stockCrystal;
    }

    public int stockOre() {
        return stockOre;
    }

    public int stockHerbs() {
        return stockHerbs;
    }

    public int nonHomeZoneCount() {
        int count = 0;
        for (ColonyZone zone : zones) {
            if (zone.type() != ZoneType.HOME) {
                count += 1;
            }
        }
        return count;
    }

    public int countCompletedStructures(BlueprintId blueprintId) {
        int count = 0;
        for (PlacedStructure structure : structures) {
            if (structure.blueprintId() == blueprintId && structure.complete()) {
                count += 1;
            }
        }
        return count;
    }

    public int upgradedHotspotCount() {
        int count = 0;
        for (HotspotState hotspot : hotspots) {
            if (hotspot.tier() > 1) {
                count += 1;
            }
        }
        return count;
    }

    public int familyCount(HotspotFamily family) {
        int count = 0;
        for (HotspotState hotspot : hotspots) {
            if (hotspot.family() == family) {
                count += 1;
            }
        }
        return count;
    }

    public int hotspotsInZone(String zoneId) {
        int count = 0;
        for (HotspotState hotspot : hotspots) {
            if (zoneId.equals(hotspot.zoneId())) {
                count += 1;
            }
        }
        return count;
    }

    public boolean consumeStock(String id, int amount) {
        if (amount <= 0) {
            return true;
        }
        return switch (id) {
            case "wood" -> consumeWood(amount);
            case "stone" -> consumeStone(amount);
            case "fiber" -> consumeFiber(amount);
            case "food" -> consumeFood(amount);
            case "hide" -> consumeHide(amount);
            case "crystal" -> consumeCrystal(amount);
            case "ore" -> consumeOre(amount);
            case "herbs" -> consumeHerbs(amount);
            default -> false;
        };
    }

    public void addStock(String id, int amount) {
        int safeAmount = Math.max(0, amount);
        switch (id) {
            case "wood" -> stockWood += safeAmount;
            case "stone" -> stockStone += safeAmount;
            case "fiber" -> stockFiber += safeAmount;
            case "food" -> stockFood += safeAmount;
            case "hide" -> stockHide += safeAmount;
            case "crystal" -> stockCrystal += safeAmount;
            case "ore" -> stockOre += safeAmount;
            case "herbs" -> stockHerbs += safeAmount;
            default -> {
            }
        }
    }

    private boolean consumeWood(int amount) {
        if (stockWood < amount) {
            return false;
        }
        stockWood -= amount;
        return true;
    }

    private boolean consumeStone(int amount) {
        if (stockStone < amount) {
            return false;
        }
        stockStone -= amount;
        return true;
    }

    private boolean consumeFiber(int amount) {
        if (stockFiber < amount) {
            return false;
        }
        stockFiber -= amount;
        return true;
    }

    private boolean consumeFood(int amount) {
        if (stockFood < amount) {
            return false;
        }
        stockFood -= amount;
        return true;
    }

    private boolean consumeHide(int amount) {
        if (stockHide < amount) {
            return false;
        }
        stockHide -= amount;
        return true;
    }

    private boolean consumeCrystal(int amount) {
        if (stockCrystal < amount) {
            return false;
        }
        stockCrystal -= amount;
        return true;
    }

    private boolean consumeOre(int amount) {
        if (stockOre < amount) {
            return false;
        }
        stockOre -= amount;
        return true;
    }

    private boolean consumeHerbs(int amount) {
        if (stockHerbs < amount) {
            return false;
        }
        stockHerbs -= amount;
        return true;
    }

    private static Map<TaskType, Double> defaultTaskWeights() {
        EnumMap<TaskType, Double> defaults = new EnumMap<>(TaskType.class);
        defaults.put(TaskType.BUILD, 0.9);
        defaults.put(TaskType.FARM, 1.4);
        defaults.put(TaskType.GATHER, 1.1);
        defaults.put(TaskType.HAUL, 1.3);
        defaults.put(TaskType.DEFEND, 0.8);
        defaults.put(TaskType.REPAIR, 1.2);
        defaults.put(TaskType.EMERGENCY, 2.0);
        return defaults;
    }
}
