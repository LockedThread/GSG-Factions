package com.massivecraft.factions.tasks;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Calendar;

public class TaskForceInviteUpdate extends BukkitRunnable {

    private Calendar calendar = Calendar.getInstance();
    private int previousDay;

    public TaskForceInviteUpdate() {
        this.previousDay = calendar.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public void run() {
        int today = calendar.get(Calendar.DAY_OF_YEAR);
        if (this.previousDay != today) {
            this.previousDay = today;

            for (Faction faction : Factions.getInstance().getAllFactions()) {
                faction.setInvitesToday(0);
            }
        }
    }
}
