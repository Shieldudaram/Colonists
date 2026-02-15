package com.shieldudaram.colonists.model;

import java.util.Objects;

public record ItemRequirement(String id, int minTier, int minQuality, int qty) {
    public ItemRequirement {
        Objects.requireNonNull(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (minTier < 1 || minTier > 3) {
            throw new IllegalArgumentException("minTier must be between 1 and 3");
        }
        if (minQuality < 1 || minQuality > 5) {
            throw new IllegalArgumentException("minQuality must be between 1 and 5");
        }
        if (qty <= 0) {
            throw new IllegalArgumentException("qty must be greater than 0");
        }
    }

    public boolean accepts(ItemKey key) {
        return id.equals(key.id()) && key.tier() >= minTier && key.quality() >= minQuality;
    }
}
