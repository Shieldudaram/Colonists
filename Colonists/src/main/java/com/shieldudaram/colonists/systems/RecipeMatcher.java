package com.shieldudaram.colonists.systems;

import com.shieldudaram.colonists.model.ItemKey;
import com.shieldudaram.colonists.model.ItemRequirement;

import java.util.Collection;

public final class RecipeMatcher {
    public boolean accepts(ItemRequirement requirement, Collection<ItemKey> offered) {
        for (ItemKey key : offered) {
            if (requirement.accepts(key)) {
                return true;
            }
        }
        return false;
    }
}
