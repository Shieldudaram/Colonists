package com.shieldudaram.colonists.systems;

import com.shieldudaram.colonists.model.ColonyState;
import com.shieldudaram.colonists.model.ColonyZone;
import com.shieldudaram.colonists.model.ZoneType;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public final class ZoneSystem {
    private final AtomicInteger zoneCounter = new AtomicInteger(1);

    public ColonyZone createZone(ColonyState state, ZoneType type, int x1, int z1, int x2, int z2) {
        String zoneId = "zone-" + zoneCounter.getAndIncrement();
        ColonyZone zone = new ColonyZone(zoneId, type, x1, z1, x2, z2);
        state.zones().add(zone);
        return zone;
    }

    public boolean clearZone(ColonyState state, String zoneId) {
        return state.zones().removeIf(zone -> zone.id().equals(zoneId));
    }

    public ColonyZone zoneAt(ColonyState state, int x, int z, ZoneType requiredType) {
        for (ColonyZone zone : state.zones()) {
            if (zone.contains(x, z) && zone.type() == requiredType) {
                return zone;
            }
        }
        return null;
    }

    public ZoneType parseZoneType(String input) {
        return ZoneType.valueOf(input.trim().toUpperCase(Locale.ROOT));
    }
}
