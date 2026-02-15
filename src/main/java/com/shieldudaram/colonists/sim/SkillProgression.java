package com.shieldudaram.colonists.sim;

import com.shieldudaram.colonists.model.Role;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class SkillProgression {
    private final Map<String, EnumMap<Role, Integer>> xpByCitizen;

    public SkillProgression() {
        this.xpByCitizen = new HashMap<>();
    }

    public void grantXp(String citizenId, Role role, int xp) {
        xpByCitizen
                .computeIfAbsent(citizenId, ignored -> zeroedXp())
                .merge(role, Math.max(0, xp), Integer::sum);
    }

    public int levelFor(String citizenId, Role role) {
        int xp = xpByCitizen.getOrDefault(citizenId, zeroedXp()).getOrDefault(role, 0);
        int level = 1;
        for (int threshold : ColonistsConstants.XP_THRESHOLDS) {
            if (xp >= threshold) {
                level += 1;
            }
        }
        return Math.min(10, level);
    }

    public double speedBonusFor(Role role, int level) {
        int clamped = Math.max(1, Math.min(10, level));
        int levelsAboveOne = clamped - 1;
        return switch (role) {
            case GUARD -> 1.0 + (levelsAboveOne * 0.025);
            default -> 1.0 + (levelsAboveOne * 0.03);
        };
    }

    public double guardDamageBonusMultiplier(int level) {
        int clamped = Math.max(1, Math.min(10, level));
        return 1.0 + ((clamped - 1) * 0.02);
    }

    private static EnumMap<Role, Integer> zeroedXp() {
        EnumMap<Role, Integer> map = new EnumMap<>(Role.class);
        for (Role role : Role.values()) {
            map.put(role, 0);
        }
        return map;
    }
}
