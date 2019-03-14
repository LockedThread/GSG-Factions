package com.massivecraft.factions.tasks;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TaskCorner extends BukkitRunnable {

    private final FPlayer fPlayer;
    private final List<FLocation> surrounding;
    private int amount = 0;

    public TaskCorner(FPlayer fPlayer, List<FLocation> surrounding) {
        this.fPlayer = fPlayer;
        this.surrounding = surrounding;
    }

    @Override
    public void run() {
        if (surrounding.isEmpty()) {
            fPlayer.sendMessage(TL.COMMAND_CORNER_SUCCESS.format(amount));
            cancel();
        } else if (fPlayer.isOffline()) {
            cancel();
        } else {
            FLocation fLocation = surrounding.remove(0);
            if (fPlayer.attemptClaim(fPlayer.getFaction(), fLocation, true)) {
                amount++;
            }
        }
    }
}
