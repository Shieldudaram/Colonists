package com.shieldudaram.colonists.model;

import java.util.Objects;

public final class ColonyZone {
    private final String id;
    private ZoneType type;
    private final int minX;
    private final int minZ;
    private final int maxX;
    private final int maxZ;

    public ColonyZone(String id, ZoneType type, int minX, int minZ, int maxX, int maxZ) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.minX = Math.min(minX, maxX);
        this.minZ = Math.min(minZ, maxZ);
        this.maxX = Math.max(minX, maxX);
        this.maxZ = Math.max(minZ, maxZ);
    }

    public String id() {
        return id;
    }

    public ZoneType type() {
        return type;
    }

    public void setType(ZoneType type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    public boolean contains(int x, int z) {
        return x >= minX && x <= maxX && z >= minZ && z <= maxZ;
    }
}
