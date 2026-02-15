package com.shieldudaram.colonists.sim;

import com.shieldudaram.colonists.model.UnlockStage;

public final class ProgressionGateEvaluator {
    public UnlockStage currentStage(int population, int watchtowers, int upgradedHotspots, int raidsSurvived) {
        if (population >= 5 && raidsSurvived >= 3 && upgradedHotspots >= 4) {
            return UnlockStage.STAGE_3;
        }
        if (population >= 3 && watchtowers >= 1 && upgradedHotspots >= 2) {
            return UnlockStage.STAGE_2;
        }
        return UnlockStage.STAGE_1;
    }
}
