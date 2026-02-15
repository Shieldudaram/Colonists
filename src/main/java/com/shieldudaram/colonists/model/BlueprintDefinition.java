package com.shieldudaram.colonists.model;

import java.util.List;

public record BlueprintDefinition(
        BlueprintId id,
        UnlockStage stage,
        int footprintX,
        int footprintZ,
        int buildTimeSeconds,
        List<ItemRequirement> cost,
        String gameplayEffect
) {
}
