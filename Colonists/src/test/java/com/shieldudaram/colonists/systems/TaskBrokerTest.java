package com.shieldudaram.colonists.systems;

import com.shieldudaram.colonists.model.CitizenState;
import com.shieldudaram.colonists.model.ColonyState;
import com.shieldudaram.colonists.model.ColonyTask;
import com.shieldudaram.colonists.model.Role;
import com.shieldudaram.colonists.model.TaskStatus;
import com.shieldudaram.colonists.model.TaskType;
import com.shieldudaram.colonists.sim.ColonyCallbacks;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskBrokerTest {
    @Test
    void emergencyTaskCanPreemptEvenWithLock() {
        ColonyState state = new ColonyState();
        CitizenState citizen = new CitizenState("citizen-1", Role.BUILDER);
        state.citizens().add(citizen);
        state.setWorldTimeSec(100);

        TaskBroker broker = new TaskBroker();
        ColonyTask low = broker.createTask(state, TaskType.BUILD, "a", 0.5, false, new ColonyCallbacks() {
        });
        low.reserve(citizen.id());
        low.setStatus(TaskStatus.RUNNING);

        citizen.setPreemptLockUntilSec(state.worldTimeSec() + 100);
        ColonyTask emergency = broker.createTask(state, TaskType.EMERGENCY, "b", 2.0, true, new ColonyCallbacks() {
        });

        broker.assignTasks(state, new ColonyCallbacks() {
        });

        assertEquals(TaskStatus.QUEUED, low.status());
        assertEquals(citizen.id(), emergency.reservedByCitizenId());
        assertEquals(TaskStatus.RUNNING, emergency.status());
    }
}
