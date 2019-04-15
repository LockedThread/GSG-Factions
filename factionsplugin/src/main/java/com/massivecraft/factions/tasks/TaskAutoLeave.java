package com.massivecraft.factions.tasks;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.util.AutoLeaveProcessTask;

public class TaskAutoLeave implements Runnable {

    private static AutoLeaveProcessTask task;
    private double rate;

    public TaskAutoLeave() {
        this.rate = Conf.autoLeaveRoutineRunsEveryXMinutes;
    }

    public synchronized void run() {
        if (task != null && !task.isFinished()) {
            return;
        }

        task = new AutoLeaveProcessTask();
        task.runTaskTimer(P.p, 1, 1);

        // maybe setting has been changed? if so, restart this task at new rate
        if (this.rate != Conf.autoLeaveRoutineRunsEveryXMinutes) {
            P.p.startAutoLeaveTask(true);
        }
    }
}
