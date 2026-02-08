package com.shieldudaram.colonists.systems;

import com.shieldudaram.colonists.model.ColonyState;
import com.shieldudaram.colonists.model.HotspotFamily;
import com.shieldudaram.colonists.model.HotspotState;
import com.shieldudaram.colonists.model.ZoneType;
import com.shieldudaram.colonists.sim.ColonyCallbacks;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HotspotSystemTest {
    @Test
    void firstHarvestStartsTimerAndResetRestoresHotspot() {
        ColonyState state = new ColonyState();
        ZoneSystem zoneSystem = new ZoneSystem();
        HotspotSystem hotspotSystem = new HotspotSystem();

        zoneSystem.createZone(state, ZoneType.HOTSPOT, 0, 0, 20, 20);
        HotspotState hotspot = hotspotSystem.placeHotspot(state, HotspotFamily.WOOD, 5, 5, new ColonyCallbacks() {
        });

        state.setWorldTimeSec(10);
        HotspotSystem.HarvestResult result = hotspotSystem.harvest(state, hotspot.id(), "citizen-1", 1, new ColonyCallbacks() {
        });

        assertTrue(result.quantity() > 0);
        assertNotNull(hotspot.cycleStartedAtSec());
        assertNotNull(hotspot.resetAtSec());

        long resetAt = hotspot.resetAtSec();
        state.setWorldTimeSec(resetAt);
        hotspotSystem.tick(state, new ColonyCallbacks() {
        });

        assertEquals(hotspot.capacityMax(), hotspot.capacityNow());
        assertEquals(1.0, hotspot.degradation());
        assertNull(hotspot.cycleStartedAtSec());
        assertNull(hotspot.resetAtSec());
    }
}
