package com.shieldudaram.colonists.content;

public final class ColonistsConfig {
    public int schemaVersion = 1;
    public Sim sim = new Sim();
    public Limits limits = new Limits();
    public Time time = new Time();
    public Save save = new Save();
    public Threat threat = new Threat();

    public static final class Sim {
        public int tickHz = 5;
        public boolean aiStaggerEnabled = true;
        public int aiAgentsPerTick = 2;
        public int pathReplanIntervalMs = 1000;
    }

    public static final class Limits {
        public int maxCitizens = 5;
        public int maxHotspotsPerFamily = 1;
        public int maxActiveRaidEnemies = 1;
        public int hotspotSpacingMeters = 12;
        public int hotspotPerZoneCap = 2;
    }

    public static final class Time {
        public int dayLengthMinutes = 24;
        public int workStartHour = 6;
        public int workEndHour = 20;
        public int sleepStartHour = 22;
        public int sleepEndHour = 6;
    }

    public static final class Save {
        public int autosaveSeconds = 300;
        public int backupRotations = 5;
    }

    public static final class Threat {
        public int graceSeconds = 1800;
        public int baseIntervalSeconds = 900;
        public int triggerCooldownSeconds = 480;
    }
}
