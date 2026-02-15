package com.shieldudaram.colonists.model;

import java.util.Objects;

public record ItemKey(String id, int tier, int quality) {
    public ItemKey {
        Objects.requireNonNull(id, "id");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        if (tier < 1 || tier > 3) {
            throw new IllegalArgumentException("tier must be between 1 and 3");
        }
        if (quality < 1 || quality > 5) {
            throw new IllegalArgumentException("quality must be between 1 and 5");
        }
    }
}
