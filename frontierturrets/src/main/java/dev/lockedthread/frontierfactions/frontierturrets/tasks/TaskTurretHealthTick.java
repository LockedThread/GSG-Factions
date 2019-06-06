package dev.lockedthread.frontierfactions.frontierturrets.tasks;

import dev.lockedthread.frontierfactions.frontierturrets.FrontierTurrets;
import dev.lockedthread.frontierfactions.frontierturrets.objs.Turret;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskTurretHealthTick extends BukkitRunnable {

    private static final FrontierTurrets FRONTIER_TURRETS = FrontierTurrets.getInstance();

    @Override
    public void run() {
        for (Turret turret : FRONTIER_TURRETS.getUnitTurrets().getTurretMap().values()) {
            turret.tickHealth();
        }
    }
}
