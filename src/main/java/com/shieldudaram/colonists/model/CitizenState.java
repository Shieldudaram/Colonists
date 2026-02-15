package com.shieldudaram.colonists.model;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class CitizenState {
    private final String id;
    private Role primaryRole;
    private final EnumMap<Role, Integer> skills;
    private final CitizenNeeds needs;
    private long preemptLockUntilSec;

    public CitizenState(String id, Role primaryRole) {
        this(id, primaryRole, defaultSkills(), new CitizenNeeds(100.0, 100.0, 100.0), 0L);
    }

    public CitizenState(
            String id,
            Role primaryRole,
            Map<Role, Integer> skills,
            CitizenNeeds needs,
            long preemptLockUntilSec
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.primaryRole = Objects.requireNonNull(primaryRole, "primaryRole");
        this.skills = new EnumMap<>(Role.class);
        for (Role role : Role.values()) {
            int skill = skills.getOrDefault(role, 1);
            this.skills.put(role, clampSkill(skill));
        }
        this.needs = Objects.requireNonNull(needs, "needs");
        this.preemptLockUntilSec = Math.max(0L, preemptLockUntilSec);
    }

    public String id() {
        return id;
    }

    public Role primaryRole() {
        return primaryRole;
    }

    public void setPrimaryRole(Role primaryRole) {
        this.primaryRole = Objects.requireNonNull(primaryRole, "primaryRole");
    }

    public int skill(Role role) {
        return skills.get(role);
    }

    public void setSkill(Role role, int value) {
        skills.put(Objects.requireNonNull(role, "role"), clampSkill(value));
    }

    public Map<Role, Integer> skills() {
        return Map.copyOf(skills);
    }

    public CitizenNeeds needs() {
        return needs;
    }

    public long preemptLockUntilSec() {
        return preemptLockUntilSec;
    }

    public void setPreemptLockUntilSec(long preemptLockUntilSec) {
        this.preemptLockUntilSec = Math.max(0L, preemptLockUntilSec);
    }

    public double speedMultiplier() {
        double needFactor = (needs.food() + needs.rest() + needs.safety()) / 300.0;
        return Math.max(0.2, needFactor);
    }

    private static Map<Role, Integer> defaultSkills() {
        EnumMap<Role, Integer> defaults = new EnumMap<>(Role.class);
        for (Role role : Role.values()) {
            defaults.put(role, 1);
        }
        return defaults;
    }

    private static int clampSkill(int value) {
        return Math.max(1, Math.min(10, value));
    }
}
