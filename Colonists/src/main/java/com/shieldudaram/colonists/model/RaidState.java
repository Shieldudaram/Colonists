package com.shieldudaram.colonists.model;

public final class RaidState {
    private long nextRaidAtSec;
    private int threatScore;
    private int activeEnemies;
    private int raidsSurvived;
    private long lastRaidAtSec;
    private int threatAtLastRaid;

    public long nextRaidAtSec() {
        return nextRaidAtSec;
    }

    public void setNextRaidAtSec(long nextRaidAtSec) {
        this.nextRaidAtSec = Math.max(0L, nextRaidAtSec);
    }

    public int threatScore() {
        return threatScore;
    }

    public void setThreatScore(int threatScore) {
        this.threatScore = Math.max(0, threatScore);
    }

    public int activeEnemies() {
        return activeEnemies;
    }

    public void setActiveEnemies(int activeEnemies) {
        this.activeEnemies = Math.max(0, Math.min(1, activeEnemies));
    }

    public int raidsSurvived() {
        return raidsSurvived;
    }

    public void setRaidsSurvived(int raidsSurvived) {
        this.raidsSurvived = Math.max(0, raidsSurvived);
    }

    public long lastRaidAtSec() {
        return lastRaidAtSec;
    }

    public void setLastRaidAtSec(long lastRaidAtSec) {
        this.lastRaidAtSec = Math.max(0L, lastRaidAtSec);
    }

    public int threatAtLastRaid() {
        return threatAtLastRaid;
    }

    public void setThreatAtLastRaid(int threatAtLastRaid) {
        this.threatAtLastRaid = Math.max(0, threatAtLastRaid);
    }
}
