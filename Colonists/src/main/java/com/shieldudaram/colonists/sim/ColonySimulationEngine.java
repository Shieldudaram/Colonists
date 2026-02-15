package com.shieldudaram.colonists.sim;

import com.shieldudaram.colonists.model.BlueprintId;
import com.shieldudaram.colonists.model.ColonyState;
import com.shieldudaram.colonists.model.ColonyTask;
import com.shieldudaram.colonists.model.ColonyZone;
import com.shieldudaram.colonists.model.HotspotFamily;
import com.shieldudaram.colonists.model.HotspotState;
import com.shieldudaram.colonists.model.PlacedStructure;
import com.shieldudaram.colonists.model.PolicyId;
import com.shieldudaram.colonists.model.PolicyWeights;
import com.shieldudaram.colonists.model.Role;
import com.shieldudaram.colonists.model.TaskType;
import com.shieldudaram.colonists.model.ZoneType;
import com.shieldudaram.colonists.save.ColonySaveService;
import com.shieldudaram.colonists.systems.HotspotSystem;
import com.shieldudaram.colonists.systems.InsuranceSystem;
import com.shieldudaram.colonists.systems.RaidDirector;
import com.shieldudaram.colonists.systems.TaskBroker;
import com.shieldudaram.colonists.systems.ZoneSystem;
import com.shieldudaram.colonists.telemetry.TelemetryMode;
import com.shieldudaram.colonists.telemetry.TelemetryService;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.UUID;

public final class ColonySimulationEngine {
    private final ColonyState state;
    private final ColonyCallbacks callbacks;
    private final TaskBroker taskBroker;
    private final ZoneSystem zoneSystem;
    private final HotspotSystem hotspotSystem;
    private final RaidDirector raidDirector;
    private final InsuranceSystem insuranceSystem;
    private final SkillProgression skillProgression;
    private final ProgressionGateEvaluator progressionGateEvaluator;
    private final BlueprintCatalog blueprintCatalog;
    private final ColonySaveService saveService;
    private final TelemetryService telemetry;
    private final Path saveDir;
    private final Deque<Runnable> pauseQueue;
    private final boolean autosaveEnabled;

    private long tickCounter;
    private long lastAutosaveAt;

    public ColonySimulationEngine(Path logsDir, Path saveDir, ColonyCallbacks callbacks, boolean autosaveEnabled) {
        this.state = new ColonyState();
        this.callbacks = callbacks;
        this.taskBroker = new TaskBroker();
        this.zoneSystem = new ZoneSystem();
        this.hotspotSystem = new HotspotSystem();
        this.raidDirector = new RaidDirector();
        this.insuranceSystem = new InsuranceSystem();
        this.skillProgression = new SkillProgression();
        this.progressionGateEvaluator = new ProgressionGateEvaluator();
        this.blueprintCatalog = new BlueprintCatalog();
        this.saveService = new ColonySaveService();
        this.telemetry = new TelemetryService(logsDir);
        this.saveDir = saveDir;
        this.pauseQueue = new ArrayDeque<>();
        this.autosaveEnabled = autosaveEnabled;
        bootstrap();
    }

    public ColonyState state() {
        return state;
    }

    public TelemetryService telemetry() {
        return telemetry;
    }

    public void tick() {
        tickCounter += 1;
        long elapsedSeconds = tickCounter / ColonistsConstants.TICK_HZ;
        state.setWorldTimeSec(elapsedSeconds);
        ColonyContext context = new ColonyContext(tickCounter, state.worldTimeSec(), state.paused(), 0L);
        callbacks.onPreTick(context);

        if (state.paused()) {
            callbacks.onPostTick(context);
            return;
        }

        executePauseQueue();
        refreshPopulationCap();
        taskBroker.assignTasks(state, callbacks);
        hotspotSystem.tick(state, callbacks);
        raidDirector.tick(state, callbacks);

        maybeAutosave();
        callbacks.onPostTick(context);
    }

    public void queueWhilePaused(Runnable action) {
        pauseQueue.add(action);
    }

    public void setPaused(boolean paused) {
        state.setPaused(paused);
    }

    public ColonyZone createZone(ZoneType type, int x1, int z1, int x2, int z2) {
        return zoneSystem.createZone(state, type, x1, z1, x2, z2);
    }

    public boolean clearZone(String zoneId) {
        return zoneSystem.clearZone(state, zoneId);
    }

    public HotspotState placeHotspot(HotspotFamily family, int x, int z) {
        return hotspotSystem.placeHotspot(state, family, x, z, callbacks);
    }

    public HotspotState upgradeHotspot(String hotspotId) {
        return hotspotSystem.upgradeHotspot(state, hotspotId, callbacks);
    }

    public HotspotSystem.HarvestResult harvest(String hotspotId, String citizenId) {
        int gatherSkill = 1;
        for (var citizen : state.citizens()) {
            if (citizen.id().equals(citizenId)) {
                gatherSkill = citizen.skill(Role.GATHERER);
                break;
            }
        }
        HotspotSystem.HarvestResult result = hotspotSystem.harvest(state, hotspotId, citizenId, gatherSkill, callbacks);
        skillProgression.grantXp(citizenId, Role.GATHERER, ColonistsConstants.XP_GATHERER);
        return result;
    }

    public ColonyTask createTask(TaskType type, String targetId, double basePriority, boolean emergency) {
        return taskBroker.createTask(state, type, targetId, basePriority, emergency, callbacks);
    }

    public void applyPolicy(PolicyId policyId) {
        PolicyWeights weights = ColonistsConstants.policyWeights(policyId);
        state.setActivePolicy(policyId);
        state.setTaskWeights(weights);
        callbacks.onPolicyChanged(policyId.name());
    }

    public void setPriority(TaskType taskType, double value) {
        state.setTaskWeights(state.taskWeights().withWeight(taskType, value));
    }

    public PlacedStructure placeBlueprint(BlueprintId blueprintId, int x, int z, int rotation) {
        var definition = blueprintCatalog.get(blueprintId);
        if (definition == null) {
            throw new IllegalArgumentException("Unknown blueprint: " + blueprintId);
        }

        for (var requirement : definition.cost()) {
            if (!hasStock(requirement.id(), requirement.qty())) {
                throw new IllegalStateException("Missing stock for " + requirement.id());
            }
        }
        for (var requirement : definition.cost()) {
            state.consumeStock(requirement.id(), requirement.qty());
        }

        long now = state.worldTimeSec();
        PlacedStructure structure = new PlacedStructure(
                "structure-" + UUID.randomUUID(),
                blueprintId,
                x,
                z,
                rotation,
                definition.buildTimeSeconds() == 0,
                now,
                now + definition.buildTimeSeconds()
        );
        state.structures().add(structure);
        createTask(TaskType.BUILD, structure.id(), 1.0, false);
        return structure;
    }

    public void completeStructure(String structureId, String citizenId) {
        for (PlacedStructure structure : state.structures()) {
            if (structure.id().equals(structureId)) {
                structure.setComplete(true);
                skillProgression.grantXp(citizenId, Role.BUILDER, ColonistsConstants.XP_BUILDER);
                break;
            }
        }
    }

    public int currentUnlockStageOrdinal() {
        int watchtowers = state.countCompletedStructures(BlueprintId.WATCHTOWER);
        int upgradedHotspots = state.upgradedHotspotCount();
        int survived = state.raidState().raidsSurvived();
        return progressionGateEvaluator.currentStage(
                state.populationCurrent(),
                watchtowers,
                upgradedHotspots,
                survived
        ).ordinal();
    }

    public void resolveRaid(boolean success) {
        raidDirector.resolveRaid(state, success, callbacks);
    }

    public void handleCitizenDeath(String citizenId, String cause) {
        for (var citizen : state.citizens()) {
            if (citizen.id().equals(citizenId)) {
                insuranceSystem.handleCitizenDeath(state, citizen, cause, callbacks);
                break;
            }
        }
    }

    public void saveNow() {
        saveService.save(state, saveDir);
        lastAutosaveAt = state.worldTimeSec();
    }

    public void load() {
        saveService.load(state, saveDir);
    }

    public void setTelemetryMode(TelemetryMode mode) {
        telemetry.setMode(mode);
    }

    public String status(TelemetryMode mode) {
        return telemetry.status(state, mode);
    }

    public ZoneType parseZoneType(String value) {
        return zoneSystem.parseZoneType(value);
    }

    public HotspotFamily parseFamily(String value) {
        return hotspotSystem.parseFamily(value);
    }

    public TaskType parseTaskType(String value) {
        return TaskType.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    public PolicyId parsePolicyId(String value) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "FORTIFY" -> PolicyId.FORTIFY;
            case "HARVESTRUSH", "HARVEST_RUSH" -> PolicyId.HARVEST_RUSH;
            case "RECOVERY" -> PolicyId.RECOVERY;
            default -> throw new IllegalArgumentException("Unknown policy: " + value);
        };
    }

    public BlueprintId parseBlueprintId(String value) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return BlueprintId.valueOf(normalized.replace('-', '_'));
    }

    private void bootstrap() {
        for (int i = 0; i < ColonistsConstants.STARTING_CITIZENS; i++) {
            state.citizens().add(new com.shieldudaram.colonists.model.CitizenState("citizen-" + (i + 1), Role.BUILDER));
        }

        PlacedStructure townCore = new PlacedStructure(
                "structure-town-core",
                BlueprintId.TOWN_CORE,
                0,
                0,
                0,
                true,
                0L,
                0L
        );
        PlacedStructure house = new PlacedStructure(
                "structure-house-bootstrap",
                BlueprintId.HOUSE,
                6,
                0,
                0,
                true,
                0L,
                0L
        );
        state.structures().add(townCore);
        state.structures().add(house);
    }

    private void executePauseQueue() {
        while (!pauseQueue.isEmpty()) {
            pauseQueue.removeFirst().run();
        }
    }

    private void refreshPopulationCap() {
        int houseCount = state.countCompletedStructures(BlueprintId.HOUSE);
        int cap = 2 + (houseCount * 2);
        state.setPopulationCap(cap);
        while (state.populationCurrent() > state.populationCap()) {
            state.citizens().remove(state.citizens().size() - 1);
        }
    }

    private void maybeAutosave() {
        if (!autosaveEnabled) return;
        if (state.worldTimeSec() - lastAutosaveAt >= ColonistsConstants.AUTOSAVE_SECONDS) {
            saveNow();
        }
    }

    private boolean hasStock(String id, int qty) {
        return switch (id) {
            case "wood" -> state.stockWood() >= qty;
            case "stone" -> state.stockStone() >= qty;
            case "fiber" -> state.stockFiber() >= qty;
            case "food" -> state.stockFood() >= qty;
            case "hide" -> state.stockHide() >= qty;
            case "crystal" -> state.stockCrystal() >= qty;
            case "ore" -> state.stockOre() >= qty;
            case "herbs" -> state.stockHerbs() >= qty;
            default -> false;
        };
    }
}
