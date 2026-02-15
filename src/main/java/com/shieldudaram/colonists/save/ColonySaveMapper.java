package com.shieldudaram.colonists.save;

import com.shieldudaram.colonists.model.CitizenState;
import com.shieldudaram.colonists.model.ColonyState;
import com.shieldudaram.colonists.model.PolicyId;
import com.shieldudaram.colonists.sim.ColonistsConstants;

import java.util.ArrayList;

public final class ColonySaveMapper {
    public ColonySaveV1 toSave(ColonyState state) {
        ColonySaveV1 save = new ColonySaveV1();
        save.schemaVersion = ColonistsConstants.SCHEMA_VERSION;
        save.worldTimeSec = state.worldTimeSec();
        save.colony = new ColonySaveV1.ColonyEnvelope();
        save.colony.populationCap = state.populationCap();
        save.colony.activePolicy = state.activePolicy().name();
        save.colony.stockWood = state.stockWood();
        save.colony.stockStone = state.stockStone();
        save.colony.stockFiber = state.stockFiber();
        save.colony.stockFood = state.stockFood();
        save.colony.stockHide = state.stockHide();
        save.colony.stockCrystal = state.stockCrystal();
        save.citizens = new ArrayList<>(state.citizens());
        save.hotspots = new ArrayList<>(state.hotspots());
        save.tasks = new ArrayList<>(state.tasks());
        save.raid = state.raidState();
        save.insurance = new ColonySaveV1.InsuranceEnvelope();
        save.insurance.reservePoints = state.insuranceState().reservePoints();
        save.insurance.claims = new ArrayList<>(state.insuranceState().claimHistory());
        return save;
    }

    public void applySave(ColonyState state, ColonySaveV1 save) {
        if (save.schemaVersion != ColonistsConstants.SCHEMA_VERSION) {
            throw new IllegalStateException(
                    "Unsupported schema version " + save.schemaVersion + ", expected " + ColonistsConstants.SCHEMA_VERSION
            );
        }

        state.setWorldTimeSec(save.worldTimeSec);
        state.setPopulationCap(save.colony.populationCap);
        state.setActivePolicy(PolicyId.valueOf(save.colony.activePolicy));
        state.citizens().clear();
        state.hotspots().clear();
        state.tasks().clear();
        state.citizens().addAll(save.citizens);
        state.hotspots().addAll(save.hotspots);
        state.tasks().addAll(save.tasks);
        for (CitizenState citizen : state.citizens()) {
            citizen.setPreemptLockUntilSec(Math.max(0, citizen.preemptLockUntilSec()));
        }
    }
}
