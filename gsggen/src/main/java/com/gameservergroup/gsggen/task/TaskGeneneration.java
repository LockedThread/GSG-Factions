package com.gameservergroup.gsggen.task;

import com.gameservergroup.gsggen.GSGGen;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskGeneneration extends BukkitRunnable {

    private static final GSGGen GSG_GEN = GSGGen.getInstance();

    @Override
    public void run() {
        GSG_GEN.getUnitGen().getGenerations().removeIf(next -> !next.generate());
    }
}
