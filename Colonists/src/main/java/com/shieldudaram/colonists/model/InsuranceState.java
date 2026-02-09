package com.shieldudaram.colonists.model;

import java.util.ArrayList;
import java.util.List;

public final class InsuranceState {
    public static final int CLAIM_COST = 10;

    private int reservePoints;
    private final List<String> claimHistory;

    public InsuranceState(int reservePoints) {
        this.reservePoints = reservePoints;
        this.claimHistory = new ArrayList<>();
    }

    public int reservePoints() {
        return reservePoints;
    }

    public List<String> claimHistory() {
        return List.copyOf(claimHistory);
    }

    public void applyClaim(String citizenId) {
        reservePoints -= CLAIM_COST;
        claimHistory.add(citizenId);
    }

    public void addReserve(int points) {
        reservePoints += points;
    }
}
