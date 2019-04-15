package com.massivecraft.factions.tasks;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import org.bukkit.scheduler.BukkitRunnable;

public class TaskWallCheckReminder extends BukkitRunnable {

    private int revolution = 0;

    @Override
    public void run() {
        revolution++;
        for (Faction faction : Factions.getInstance().getAllFactions()) {
            if (faction.getCheckReminderMinutes() != 0) {
                if (revolution % faction.getCheckReminderMinutes() == 0) {
                    faction.sendCheckRemind();
                }
            }
        }
    }
}
