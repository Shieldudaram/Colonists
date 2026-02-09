package com.shieldudaram.colonists.model;

import java.util.Objects;

public record ItemStack(ItemKey key, int qty) {
    public ItemStack {
        Objects.requireNonNull(key, "key");
        if (qty <= 0) {
            throw new IllegalArgumentException("qty must be greater than 0");
        }
    }
}
