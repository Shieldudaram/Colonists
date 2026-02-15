package com.shieldudaram.colonists.systems;

import com.shieldudaram.colonists.model.CitizenState;
import com.shieldudaram.colonists.model.ColonyState;
import com.shieldudaram.colonists.model.ColonyTask;
import com.shieldudaram.colonists.model.TaskStatus;
import com.shieldudaram.colonists.model.TaskType;
import com.shieldudaram.colonists.sim.ColonistsConstants;
import com.shieldudaram.colonists.sim.ColonyCallbacks;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class TaskBroker {
    public ColonyTask createTask(ColonyState state, TaskType type, String targetId, double basePriority, boolean emergency, ColonyCallbacks callbacks) {
        ColonyTask task = new ColonyTask(
                "task-" + UUID.randomUUID(),
                type,
                targetId,
                basePriority,
                emergency
        );
        state.tasks().add(task);
        callbacks.onTaskCreated(task.id());
        return task;
    }

    public void assignTasks(ColonyState state, ColonyCallbacks callbacks) {
        long now = state.worldTimeSec();
        List<ColonyTask> taskPool = state.tasks().stream()
                .filter(task -> task.status() == TaskStatus.QUEUED)
                .filter(task -> !task.isQuarantined(now))
                .sorted(Comparator.comparingDouble((ColonyTask task) -> scoredPriority(state, task)).reversed())
                .toList();

        for (CitizenState citizen : state.citizens()) {
            Optional<ColonyTask> activeTask = activeTaskForCitizen(state, citizen.id());
            if (activeTask.isPresent()) {
                ColonyTask current = activeTask.get();
                Optional<ColonyTask> better = taskPool.stream()
                        .filter(task -> canPreempt(citizen, task, now))
                        .filter(task -> scoredPriority(state, task) > scoredPriority(state, current))
                        .findFirst();
                if (better.isPresent()) {
                    ColonyTask replacement = better.get();
                    current.setStatus(TaskStatus.PREEMPTED);
                    current.clearReservation();
                    citizen.setPreemptLockUntilSec(now + ColonistsConstants.TASK_PREEMPT_LOCK_SECONDS);
                    callbacks.onTaskPreempted(current.id(), citizen.id(), "higher-priority");
                    reserveTask(replacement, citizen, callbacks);
                }
                continue;
            }

            Optional<ColonyTask> nextTask = taskPool.stream()
                    .filter(task -> task.reservedByCitizenId() == null)
                    .findFirst();

            nextTask.ifPresent(task -> reserveTask(task, citizen, callbacks));
        }
    }

    public void markPathFailure(ColonyState state, ColonyTask task) {
        task.incrementPathRetryCount();
        if (task.pathRetryCount() > ColonistsConstants.TASK_PATH_RETRIES) {
            task.setQuarantineUntilSec(state.worldTimeSec() + ColonistsConstants.TASK_QUARANTINE_SECONDS);
            task.resetPathRetryCount();
            task.setStatus(TaskStatus.FAILED);
            task.clearReservation();
        }
    }

    public void completeTask(ColonyTask task, String citizenId, ColonyCallbacks callbacks) {
        task.setStatus(TaskStatus.DONE);
        callbacks.onTaskCompleted(task.id(), citizenId);
    }

    private void reserveTask(ColonyTask task, CitizenState citizen, ColonyCallbacks callbacks) {
        task.reserve(citizen.id());
        task.setStatus(TaskStatus.RUNNING);
        callbacks.onTaskAssigned(task.id(), citizen.id());
    }

    private Optional<ColonyTask> activeTaskForCitizen(ColonyState state, String citizenId) {
        return state.tasks().stream()
                .filter(task -> citizenId.equals(task.reservedByCitizenId()))
                .filter(task -> task.status() == TaskStatus.RESERVED || task.status() == TaskStatus.RUNNING)
                .findFirst();
    }

    private boolean canPreempt(CitizenState citizen, ColonyTask candidate, long now) {
        if (candidate.emergency()) {
            return true;
        }
        return citizen.preemptLockUntilSec() <= now;
    }

    private double scoredPriority(ColonyState state, ColonyTask task) {
        return task.basePriority() * state.taskWeights().weightFor(task.type());
    }
}
