package com.shieldudaram.colonists.systems;

import com.shieldudaram.colonists.model.ItemKey;
import com.shieldudaram.colonists.model.ItemRequirement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecipeMatcherTest {
    private final RecipeMatcher matcher = new RecipeMatcher();

    @Test
    void acceptsItemsMeetingMinTierAndQuality() {
        ItemRequirement requirement = new ItemRequirement("wood", 2, 3, 1);
        List<ItemKey> offered = List.of(
                new ItemKey("wood", 1, 5),
                new ItemKey("wood", 2, 3)
        );

        assertTrue(matcher.accepts(requirement, offered));
    }

    @Test
    void rejectsItemsBelowThresholds() {
        ItemRequirement requirement = new ItemRequirement("ore", 2, 2, 1);
        List<ItemKey> offered = List.of(
                new ItemKey("ore", 1, 5),
                new ItemKey("ore", 2, 1)
        );

        assertFalse(matcher.accepts(requirement, offered));
    }
}
