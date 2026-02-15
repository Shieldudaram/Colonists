package com.shieldudaram.colonists.save;

import com.shieldudaram.colonists.model.CitizenState;
import com.shieldudaram.colonists.model.ColonyTask;
import com.shieldudaram.colonists.model.HotspotState;
import com.shieldudaram.colonists.model.RaidState;

import java.util.ArrayList;
import java.util.List;

public final class ColonySaveV1 {
    public int schemaVersion;
    public long worldTimeSec;
    public ColonyEnvelope colony;
    public List<CitizenState> citizens;
    public List<HotspotState> hotspots;
    public List<ColonyTask> tasks;
    public RaidState raid;
    public InsuranceEnvelope insurance;

    public ColonySaveV1() {
        this.citizens = new ArrayList<>();
        this.hotspots = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public static final class ColonyEnvelope {
        public int populationCap;
        public String activePolicy;
        public int stockWood;
        public int stockStone;
        public int stockFiber;
        public int stockFood;
        public int stockHide;
        public int stockCrystal;
    }

    public static final class InsuranceEnvelope {
        public int reservePoints;
        public List<String> claims = new ArrayList<>();
    }
}
