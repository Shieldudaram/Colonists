package com.shieldudaram.colonists.model;

import java.util.Objects;

public final class HotspotState {
    private final String id;
    private final HotspotFamily family;
    private int tier;
    private int capacityMax;
    private int capacityNow;
    private double degradation;
    private Long cycleStartedAtSec;
    private Long resetAtSec;
    private String zoneId;
    private int x;
    private int z;

    public HotspotState(
            String id,
            HotspotFamily family,
            int tier,
            int capacityMax,
            int capacityNow,
            double degradation,
            Long cycleStartedAtSec,
            Long resetAtSec,
            String zoneId,
            int x,
            int z
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.family = Objects.requireNonNull(family, "family");
        this.tier = clampTier(tier);
        this.capacityMax = Math.max(1, capacityMax);
        this.capacityNow = Math.max(0, Math.min(capacityNow, this.capacityMax));
        this.degradation = clampDegradation(degradation);
        this.cycleStartedAtSec = cycleStartedAtSec;
        this.resetAtSec = resetAtSec;
        this.zoneId = Objects.requireNonNull(zoneId, "zoneId");
        this.x = x;
        this.z = z;
    }

    public String id() {
        return id;
    }

    public HotspotFamily family() {
        return family;
    }

    public int tier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = clampTier(tier);
    }

    public int capacityMax() {
        return capacityMax;
    }

    public void setCapacityMax(int capacityMax) {
        this.capacityMax = Math.max(1, capacityMax);
        this.capacityNow = Math.min(this.capacityNow, this.capacityMax);
    }

    public int capacityNow() {
        return capacityNow;
    }

    public void setCapacityNow(int capacityNow) {
        this.capacityNow = Math.max(0, Math.min(capacityNow, capacityMax));
    }

    public double degradation() {
        return degradation;
    }

    public void setDegradation(double degradation) {
        this.degradation = clampDegradation(degradation);
    }

    public Long cycleStartedAtSec() {
        return cycleStartedAtSec;
    }

    public void setCycleStartedAtSec(Long cycleStartedAtSec) {
        this.cycleStartedAtSec = cycleStartedAtSec;
    }

    public Long resetAtSec() {
        return resetAtSec;
    }

    public void setResetAtSec(Long resetAtSec) {
        this.resetAtSec = resetAtSec;
    }

    public String zoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = Objects.requireNonNull(zoneId, "zoneId");
    }

    public int x() {
        return x;
    }

    public int z() {
        return z;
    }

    private static int clampTier(int tier) {
        return Math.max(1, Math.min(3, tier));
    }

    private static double clampDegradation(double value) {
        return Math.max(0.40, Math.min(1.0, value));
    }
}
