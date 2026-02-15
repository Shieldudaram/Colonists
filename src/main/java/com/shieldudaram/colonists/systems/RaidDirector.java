package com.shieldudaram.colonists.systems;

import com.shieldudaram.colonists.model.ColonyState;
import com.shieldudaram.colonists.model.RaidState;
import com.shieldudaram.colonists.sim.ColonistsConstants;
import com.shieldudaram.colonists.sim.ColonyCallbacks;

public final class RaidDirector {
    public void tick(ColonyState state, ColonyCallbacks callbacks) {
        RaidState raid = state.raidState();
        long now = state.worldTimeSec();

        int threatScore = (state.populationCurrent() * 4) + (state.nonHomeZoneCount() * 6);
        raid.setThreatScore(threatScore);

        if (raid.nextRaidAtSec() == 0L) {
            long scheduled = now + ColonistsConstants.RAID_GRACE_SECONDS;
            raid.setNextRaidAtSec(scheduled);
            callbacks.onRaidScheduled("raid-initial", scheduled);
            return;
        }

        boolean scheduledDue = now >= raid.nextRaidAtSec();
        boolean triggeredDue =
                (threatScore - raid.threatAtLastRaid()) >= 12
                        && (now - raid.lastRaidAtSec()) >= ColonistsConstants.RAID_TRIGGER_COOLDOWN_SECONDS
                        && now >= ColonistsConstants.RAID_GRACE_SECONDS;

        if ((scheduledDue || triggeredDue) && raid.activeEnemies() < ColonistsConstants.MAX_ACTIVE_RAID_ENEMIES) {
            startRaid(state, callbacks);
        }
    }

    public void resolveRaid(ColonyState state, boolean success, ColonyCallbacks callbacks) {
        RaidState raid = state.raidState();
        if (raid.activeEnemies() <= 0) {
            return;
        }
        raid.setActiveEnemies(0);
        if (success) {
            raid.setRaidsSurvived(raid.raidsSurvived() + 1);
        }
        callbacks.onRaidEnded("raid-" + raid.lastRaidAtSec(), success);
    }

    private void startRaid(ColonyState state, ColonyCallbacks callbacks) {
        RaidState raid = state.raidState();
        long now = state.worldTimeSec();
        raid.setActiveEnemies(ColonistsConstants.MAX_ACTIVE_RAID_ENEMIES);
        raid.setLastRaidAtSec(now);
        raid.setThreatAtLastRaid(raid.threatScore());
        raid.setNextRaidAtSec(now + ColonistsConstants.RAID_BASE_INTERVAL_SECONDS);
        callbacks.onRaidStarted("raid-" + now);
        callbacks.onRaidScheduled("raid-next", raid.nextRaidAtSec());
    }

    public int raidTier(ColonyState state) {
        int threat = state.raidState().threatScore();
        int tier = 1 + (threat / 18);
        if (tier < 1) {
            return 1;
        }
        return Math.min(5, tier);
    }

    public double healthMultiplier(int tier) {
        int clamped = Math.max(1, Math.min(5, tier));
        return 1.0 + (0.25 * (clamped - 1));
    }

    public double damageMultiplier(int tier) {
        int clamped = Math.max(1, Math.min(5, tier));
        return 1.0 + (0.15 * (clamped - 1));
    }
}
