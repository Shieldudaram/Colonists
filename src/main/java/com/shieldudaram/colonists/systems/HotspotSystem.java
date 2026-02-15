package com.shieldudaram.colonists.systems;

import com.shieldudaram.colonists.model.ColonyState;
import com.shieldudaram.colonists.model.ColonyZone;
import com.shieldudaram.colonists.model.HotspotFamily;
import com.shieldudaram.colonists.model.HotspotState;
import com.shieldudaram.colonists.model.Role;
import com.shieldudaram.colonists.model.ZoneType;
import com.shieldudaram.colonists.sim.ColonistsConstants;
import com.shieldudaram.colonists.sim.ColonyCallbacks;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public final class HotspotSystem {
    private final AtomicInteger hotspotCounter = new AtomicInteger(1);

    public HotspotState placeHotspot(ColonyState state, HotspotFamily family, int x, int z, ColonyCallbacks callbacks) {
        ColonyZone zone = findHotspotZone(state, x, z)
                .orElseThrow(() -> new IllegalStateException("Hotspot placement requires a HOTSPOT zone"));

        if (state.familyCount(family) >= ColonistsConstants.MAX_HOTSPOTS_PER_FAMILY) {
            throw new IllegalStateException("Family cap reached for " + family.name());
        }

        if (state.hotspotsInZone(zone.id()) >= ColonistsConstants.HOTSPOT_PER_ZONE_CAP) {
            throw new IllegalStateException("Per-zone hotspot cap reached for zone " + zone.id());
        }

        for (HotspotState existing : state.hotspots()) {
            int dx = existing.x() - x;
            int dz = existing.z() - z;
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance < ColonistsConstants.HOTSPOT_SPACING_METERS) {
                throw new IllegalStateException("Hotspot too close to existing site: " + existing.id());
            }
        }

        ColonistsConstants.HotspotTierProfile profile = ColonistsConstants.tierProfile(1);
        HotspotState hotspot = new HotspotState(
                "hotspot-" + hotspotCounter.getAndIncrement(),
                family,
                1,
                profile.capacity(),
                profile.capacity(),
                1.0,
                null,
                null,
                zone.id(),
                x,
                z
        );
        state.hotspots().add(hotspot);
        callbacks.onTaskCreated("hotspot-place-" + hotspot.id());
        return hotspot;
    }

    public HotspotState upgradeHotspot(ColonyState state, String hotspotId, ColonyCallbacks callbacks) {
        HotspotState hotspot = findHotspot(state, hotspotId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown hotspot: " + hotspotId));
        if (hotspot.tier() >= 3) {
            throw new IllegalStateException("Hotspot already at max tier");
        }

        int targetTier = hotspot.tier() + 1;
        if (!payUpgradeCost(state, hotspot.tier(), targetTier)) {
            throw new IllegalStateException("Insufficient stockpile for upgrade");
        }

        int previousTier = hotspot.tier();
        ColonistsConstants.HotspotTierProfile profile = ColonistsConstants.tierProfile(targetTier);
        hotspot.setTier(targetTier);
        hotspot.setCapacityMax(profile.capacity());
        hotspot.setCapacityNow(Math.min(profile.capacity(), hotspot.capacityNow() + (profile.capacity() / 5)));
        callbacks.onHotspotUpgraded(hotspot.id(), previousTier, targetTier);
        return hotspot;
    }

    public HarvestResult harvest(
            ColonyState state,
            String hotspotId,
            String citizenId,
            int gatherSkill,
            ColonyCallbacks callbacks
    ) {
        HotspotState hotspot = findHotspot(state, hotspotId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown hotspot: " + hotspotId));

        ColonistsConstants.HotspotTierProfile profile = ColonistsConstants.tierProfile(hotspot.tier());
        long now = state.worldTimeSec();
        if (hotspot.cycleStartedAtSec() == null) {
            hotspot.setCycleStartedAtSec(now);
            hotspot.setResetAtSec(now + profile.resetSeconds());
            callbacks.onHotspotFirstHarvest(hotspot.id(), hotspot.resetAtSec());
        }

        int baseYield = profile.baseYield();
        int yield = Math.max(1, (int) Math.floor(baseYield * hotspot.degradation()));
        hotspot.setCapacityNow(hotspot.capacityNow() - yield);
        hotspot.setDegradation(Math.max(profile.degradationFloor(), hotspot.degradation() - profile.degradationStep()));
        if (hotspot.capacityNow() < 0) {
            hotspot.setCapacityNow(0);
        }

        int quality = deterministicQuality(gatherSkill, profile.minQuality(), profile.maxQuality());
        state.addStock(familyToItemId(hotspot.family()), yield);
        callbacks.onHotspotHarvested(hotspot.id(), citizenId, yield);

        return new HarvestResult(yield, quality);
    }

    public void tick(ColonyState state, ColonyCallbacks callbacks) {
        long now = state.worldTimeSec();
        for (HotspotState hotspot : state.hotspots()) {
            Long resetAt = hotspot.resetAtSec();
            if (resetAt != null && now >= resetAt) {
                ColonistsConstants.HotspotTierProfile profile = ColonistsConstants.tierProfile(hotspot.tier());
                hotspot.setCapacityMax(profile.capacity());
                hotspot.setCapacityNow(profile.capacity());
                hotspot.setDegradation(1.0);
                hotspot.setCycleStartedAtSec(null);
                hotspot.setResetAtSec(null);
                callbacks.onHotspotReset(hotspot.id());
            }
        }
    }

    public HotspotFamily parseFamily(String input) {
        return HotspotFamily.valueOf(input.trim().toUpperCase(Locale.ROOT));
    }

    private Optional<ColonyZone> findHotspotZone(ColonyState state, int x, int z) {
        for (ColonyZone zone : state.zones()) {
            if (zone.type() == ZoneType.HOTSPOT && zone.contains(x, z)) {
                return Optional.of(zone);
            }
        }
        return Optional.empty();
    }

    private Optional<HotspotState> findHotspot(ColonyState state, String id) {
        return state.hotspots().stream().filter(hotspot -> hotspot.id().equals(id)).findFirst();
    }

    private boolean payUpgradeCost(ColonyState state, int fromTier, int toTier) {
        if (fromTier == 1 && toTier == 2) {
            return pay(state, "wood", 30, "stone", 20, "fiber", 10, null, 0);
        }
        if (fromTier == 2 && toTier == 3) {
            return pay(state, "wood", 50, "stone", 35, "ore", 20, "crystal", 10);
        }
        return false;
    }

    private boolean pay(
            ColonyState state,
            String firstId,
            int firstQty,
            String secondId,
            int secondQty,
            String thirdId,
            int thirdQty,
            String fourthId,
            int fourthQty
    ) {
        if (!hasEnough(state, firstId, firstQty)
                || !hasEnough(state, secondId, secondQty)
                || !hasEnough(state, thirdId, thirdQty)
                || (fourthId != null && !hasEnough(state, fourthId, fourthQty))) {
            return false;
        }
        state.consumeStock(firstId, firstQty);
        state.consumeStock(secondId, secondQty);
        state.consumeStock(thirdId, thirdQty);
        if (fourthId != null) {
            state.consumeStock(fourthId, fourthQty);
        }
        return true;
    }

    private boolean hasEnough(ColonyState state, String id, int qty) {
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

    private int deterministicQuality(int gatherSkill, int minQ, int maxQ) {
        int quality = minQ + (Math.max(1, gatherSkill) - 1) / 4;
        if (quality < minQ) {
            return minQ;
        }
        return Math.min(quality, maxQ);
    }

    private String familyToItemId(HotspotFamily family) {
        return family.name().toLowerCase(Locale.ROOT);
    }

    public record HarvestResult(int quantity, int quality) {
    }
}
