package com.shieldudaram.colonists.model;

import java.util.EnumMap;
import java.util.Map;

public final class PolicyWeights {
    private final EnumMap<TaskType, Double> weights;

    public PolicyWeights(Map<TaskType, Double> weights) {
        this.weights = new EnumMap<>(TaskType.class);
        for (TaskType type : TaskType.values()) {
            this.weights.put(type, Math.max(0.0, weights.getOrDefault(type, 1.0)));
        }
    }

    public double weightFor(TaskType type) {
        return weights.get(type);
    }

    public Map<TaskType, Double> snapshot() {
        return Map.copyOf(weights);
    }

    public PolicyWeights withWeight(TaskType type, double value) {
        EnumMap<TaskType, Double> copy = new EnumMap<>(weights);
        copy.put(type, Math.max(0.0, value));
        return new PolicyWeights(copy);
    }
}
