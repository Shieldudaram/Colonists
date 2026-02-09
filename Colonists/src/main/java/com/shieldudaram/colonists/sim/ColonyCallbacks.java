package com.shieldudaram.colonists.sim;

public interface ColonyCallbacks {
    default void onPreTick(ColonyContext context) {
    }

    default void onPostTick(ColonyContext context) {
    }

    default void onTaskCreated(String taskId) {
    }

    default void onTaskAssigned(String taskId, String citizenId) {
    }

    default void onTaskPreempted(String taskId, String fromCitizenId, String reason) {
    }

    default void onTaskCompleted(String taskId, String citizenId) {
    }

    default void onHotspotFirstHarvest(String hotspotId, long resetAtSec) {
    }

    default void onHotspotHarvested(String hotspotId, String citizenId, int yieldQty) {
    }

    default void onHotspotUpgraded(String hotspotId, int fromTier, int toTier) {
    }

    default void onHotspotReset(String hotspotId) {
    }

    default void onRaidScheduled(String raidId, long etaSec) {
    }

    default void onRaidStarted(String raidId) {
    }

    default void onRaidEnded(String raidId, boolean success) {
    }

    default void onCitizenDeath(String citizenId, String cause) {
    }

    default void onInsuranceClaimPaid(String claimId, String citizenId) {
    }

    default void onReplacementSpawned(String claimId, String newCitizenId) {
    }

    default void onPolicyChanged(String policyId) {
    }

    default void onCrisisStarted(String crisisId) {
    }

    default void onCrisisEnded(String crisisId) {
    }
}
