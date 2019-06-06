package dev.lockedthread.frontierfactions.frontiercore.units;

import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import dev.lockedthread.frontierfactions.frontiercore.FrontierCore;
import org.bukkit.event.entity.PlayerDeathEvent;

public class UnitAutoRespawn extends Unit {

    @Override
    public void setup() {
        if (FrontierCore.getInstance().getConfig().getBoolean("auto-respawn.enabled")) {
            EventPost.of(PlayerDeathEvent.class)
                    .handle(event -> FrontierCore.getInstance().getServer().getScheduler().runTaskLater(FrontierCore.getInstance(), () -> event.getEntity().spigot().respawn(), FrontierCore.getInstance().getConfig().getLong("auto-respawn.delay")))
                    .post(FrontierCore.getInstance());
        }
    }
}
