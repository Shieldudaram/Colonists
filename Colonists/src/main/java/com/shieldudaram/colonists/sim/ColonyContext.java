package com.shieldudaram.colonists.sim;

public record ColonyContext(long tick, long simTimeSec, boolean paused, long seed) {
}
