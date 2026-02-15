package com.shieldudaram.colonists.model;

public final class CitizenNeeds {
    private double food;
    private double rest;
    private double safety;

    public CitizenNeeds(double food, double rest, double safety) {
        this.food = clamp(food);
        this.rest = clamp(rest);
        this.safety = clamp(safety);
    }

    public double food() {
        return food;
    }

    public double rest() {
        return rest;
    }

    public double safety() {
        return safety;
    }

    public void setFood(double food) {
        this.food = clamp(food);
    }

    public void setRest(double rest) {
        this.rest = clamp(rest);
    }

    public void setSafety(double safety) {
        this.safety = clamp(safety);
    }

    private static double clamp(double value) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > 100.0) {
            return 100.0;
        }
        return value;
    }
}
