package com.shieldudaram.colonists.sim;

import com.shieldudaram.colonists.model.BlueprintDefinition;
import com.shieldudaram.colonists.model.BlueprintId;
import com.shieldudaram.colonists.model.ItemRequirement;
import com.shieldudaram.colonists.model.UnlockStage;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class BlueprintCatalog {
    private final EnumMap<BlueprintId, BlueprintDefinition> definitions;

    public BlueprintCatalog() {
        this.definitions = new EnumMap<>(BlueprintId.class);
        bootstrap();
    }

    public BlueprintDefinition get(BlueprintId id) {
        return definitions.get(id);
    }

    public Map<BlueprintId, BlueprintDefinition> all() {
        return Map.copyOf(definitions);
    }

    private void bootstrap() {
        definitions.put(
                BlueprintId.TOWN_CORE,
                new BlueprintDefinition(
                        BlueprintId.TOWN_CORE,
                        UnlockStage.BOOTSTRAP,
                        9,
                        9,
                        0,
                        List.of(),
                        "Colony anchor, command/rally target."
                )
        );
        definitions.put(
                BlueprintId.HOUSE,
                new BlueprintDefinition(
                        BlueprintId.HOUSE,
                        UnlockStage.STAGE_1,
                        5,
                        5,
                        90,
                        List.of(
                                new ItemRequirement("wood", 1, 1, 35),
                                new ItemRequirement("stone", 1, 1, 20),
                                new ItemRequirement("fiber", 1, 1, 10)
                        ),
                        "Adds +2 housing cap."
                )
        );
        definitions.put(
                BlueprintId.STOCKPILE,
                new BlueprintDefinition(
                        BlueprintId.STOCKPILE,
                        UnlockStage.STAGE_1,
                        7,
                        7,
                        120,
                        List.of(
                                new ItemRequirement("wood", 1, 1, 45),
                                new ItemRequirement("stone", 1, 1, 30),
                                new ItemRequirement("fiber", 1, 1, 15)
                        ),
                        "Adds +400 storage and improves haul throughput."
                )
        );
        definitions.put(
                BlueprintId.WATCHTOWER,
                new BlueprintDefinition(
                        BlueprintId.WATCHTOWER,
                        UnlockStage.STAGE_1,
                        5,
                        5,
                        110,
                        List.of(
                                new ItemRequirement("wood", 1, 1, 30),
                                new ItemRequirement("stone", 1, 1, 40),
                                new ItemRequirement("fiber", 1, 1, 5)
                        ),
                        "Adds defense rating and guard post utility."
                )
        );
        definitions.put(
                BlueprintId.FARM_SHED,
                new BlueprintDefinition(
                        BlueprintId.FARM_SHED,
                        UnlockStage.STAGE_1,
                        5,
                        5,
                        95,
                        List.of(
                                new ItemRequirement("wood", 1, 1, 30),
                                new ItemRequirement("stone", 1, 1, 15),
                                new ItemRequirement("fiber", 1, 1, 20)
                        ),
                        "Improves nearby farm task speed."
                )
        );
        definitions.put(
                BlueprintId.WORKSHOP,
                new BlueprintDefinition(
                        BlueprintId.WORKSHOP,
                        UnlockStage.STAGE_2,
                        7,
                        7,
                        140,
                        List.of(
                                new ItemRequirement("wood", 1, 1, 60),
                                new ItemRequirement("stone", 1, 1, 45),
                                new ItemRequirement("ore", 1, 1, 20)
                        ),
                        "Unlocks tiered processing recipes."
                )
        );
        definitions.put(
                BlueprintId.TRAP_POST,
                new BlueprintDefinition(
                        BlueprintId.TRAP_POST,
                        UnlockStage.STAGE_2,
                        3,
                        3,
                        70,
                        List.of(
                                new ItemRequirement("wood", 1, 1, 20),
                                new ItemRequirement("stone", 1, 1, 20),
                                new ItemRequirement("fiber", 1, 1, 10)
                        ),
                        "Deploys reusable defensive traps."
                )
        );
        definitions.put(
                BlueprintId.INFIRMARY,
                new BlueprintDefinition(
                        BlueprintId.INFIRMARY,
                        UnlockStage.STAGE_3,
                        5,
                        5,
                        130,
                        List.of(
                                new ItemRequirement("wood", 1, 1, 50),
                                new ItemRequirement("stone", 1, 1, 35),
                                new ItemRequirement("herbs", 1, 1, 15)
                        ),
                        "Increases food/rest/safety recovery rates."
                )
        );
    }
}
