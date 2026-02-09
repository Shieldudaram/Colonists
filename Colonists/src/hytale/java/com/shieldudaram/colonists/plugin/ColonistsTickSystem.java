package com.shieldudaram.colonists.plugin;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.RunWhenPausedSystem;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.shieldudaram.colonists.ColonistsRuntime;
import com.shieldudaram.colonists.sim.ColonistsConstants;

import java.util.concurrent.atomic.AtomicLong;

public final class ColonistsTickSystem extends TickingSystem<EntityStore> implements RunWhenPausedSystem<EntityStore> {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static final long STEP_NANOS = Math.max(1L, 1_000_000_000L / ColonistsConstants.TICK_HZ);

    private final ColonistsRuntime runtime;
    private final AtomicLong nextTickAtNanos = new AtomicLong(0L);

    public ColonistsTickSystem(ColonistsRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void tick(float deltaTimeSeconds, int index, Store<EntityStore> store) {
        long now = System.nanoTime();
        long nextTickAt = nextTickAtNanos.get();

        if (nextTickAt == 0L) {
            if (nextTickAtNanos.compareAndSet(0L, now + STEP_NANOS)) {
                safeTick();
            }
            return;
        }

        if (now < nextTickAt) {
            return;
        }
        if (nextTickAtNanos.compareAndSet(nextTickAt, now + STEP_NANOS)) {
            safeTick();
        }
    }

    private void safeTick() {
        try {
            runtime.tick();
        } catch (Throwable throwable) {
            LOGGER.atWarning().withCause(throwable).log("[Colonists] Runtime tick failed.");
        }
    }
}
