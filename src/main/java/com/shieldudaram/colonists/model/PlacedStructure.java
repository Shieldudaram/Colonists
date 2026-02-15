package com.shieldudaram.colonists.model;

import java.util.Objects;

public final class PlacedStructure {
    private final String id;
    private final BlueprintId blueprintId;
    private final int x;
    private final int z;
    private final int rotation;
    private boolean complete;
    private final long startedAtSec;
    private final long completesAtSec;

    public PlacedStructure(
            String id,
            BlueprintId blueprintId,
            int x,
            int z,
            int rotation,
            boolean complete,
            long startedAtSec,
            long completesAtSec
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.blueprintId = Objects.requireNonNull(blueprintId, "blueprintId");
        this.x = x;
        this.z = z;
        this.rotation = Math.floorMod(rotation, 360);
        this.complete = complete;
        this.startedAtSec = Math.max(0L, startedAtSec);
        this.completesAtSec = Math.max(0L, completesAtSec);
    }

    public String id() {
        return id;
    }

    public BlueprintId blueprintId() {
        return blueprintId;
    }

    public int x() {
        return x;
    }

    public int z() {
        return z;
    }

    public int rotation() {
        return rotation;
    }

    public boolean complete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public long startedAtSec() {
        return startedAtSec;
    }

    public long completesAtSec() {
        return completesAtSec;
    }
}
