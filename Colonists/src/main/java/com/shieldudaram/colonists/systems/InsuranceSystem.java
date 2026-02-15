package com.shieldudaram.colonists.systems;

import com.shieldudaram.colonists.model.CitizenNeeds;
import com.shieldudaram.colonists.model.CitizenState;
import com.shieldudaram.colonists.model.ColonyState;
import com.shieldudaram.colonists.model.Role;
import com.shieldudaram.colonists.sim.ColonyCallbacks;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public final class InsuranceSystem {
    public CitizenState handleCitizenDeath(ColonyState state, CitizenState deadCitizen, String cause, ColonyCallbacks callbacks) {
        callbacks.onCitizenDeath(deadCitizen.id(), cause);

        String claimId = "claim-" + UUID.randomUUID();
        state.insuranceState().applyClaim(deadCitizen.id());
        callbacks.onInsuranceClaimPaid(claimId, deadCitizen.id());

        CitizenState replacement = createReplacement(deadCitizen);
        state.citizens().removeIf(citizen -> citizen.id().equals(deadCitizen.id()));
        state.citizens().add(replacement);
        callbacks.onReplacementSpawned(claimId, replacement.id());
        return replacement;
    }

    private CitizenState createReplacement(CitizenState deadCitizen) {
        Map<Role, Integer> copiedSkills = new EnumMap<>(Role.class);
        for (Role role : Role.values()) {
            copiedSkills.put(role, deadCitizen.skill(role));
        }

        return new CitizenState(
                "citizen-" + UUID.randomUUID(),
                deadCitizen.primaryRole(),
                copiedSkills,
                new CitizenNeeds(100.0, 100.0, 100.0),
                0L
        );
    }
}
