package com.shieldudaram.colonists.model;

import java.util.Objects;

public final class ColonyTask {
    private final String id;
    private final TaskType type;
    private final String targetId;
    private final boolean emergency;
    private TaskStatus status;
    private String reservedByCitizenId;
    private int pathRetryCount;
    private long quarantineUntilSec;
    private double basePriority;

    public ColonyTask(String id, TaskType type, String targetId, double basePriority, boolean emergency) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.targetId = Objects.requireNonNull(targetId, "targetId");
        this.basePriority = Math.max(0.0, basePriority);
        this.emergency = emergency;
        this.status = TaskStatus.QUEUED;
    }

    public String id() {
        return id;
    }

    public TaskType type() {
        return type;
    }

    public String targetId() {
        return targetId;
    }

    public boolean emergency() {
        return emergency;
    }

    public TaskStatus status() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = Objects.requireNonNull(status, "status");
    }

    public String reservedByCitizenId() {
        return reservedByCitizenId;
    }

    public void reserve(String citizenId) {
        reservedByCitizenId = Objects.requireNonNull(citizenId, "citizenId");
        status = TaskStatus.RESERVED;
    }

    public void clearReservation() {
        reservedByCitizenId = null;
        if (status == TaskStatus.RESERVED || status == TaskStatus.RUNNING || status == TaskStatus.PREEMPTED) {
            status = TaskStatus.QUEUED;
        }
    }

    public int pathRetryCount() {
        return pathRetryCount;
    }

    public void incrementPathRetryCount() {
        pathRetryCount += 1;
    }

    public void resetPathRetryCount() {
        pathRetryCount = 0;
    }

    public long quarantineUntilSec() {
        return quarantineUntilSec;
    }

    public void setQuarantineUntilSec(long quarantineUntilSec) {
        this.quarantineUntilSec = Math.max(0L, quarantineUntilSec);
    }

    public boolean isQuarantined(long nowSec) {
        return quarantineUntilSec > nowSec;
    }

    public double basePriority() {
        return basePriority;
    }

    public void setBasePriority(double basePriority) {
        this.basePriority = Math.max(0.0, basePriority);
    }
}
