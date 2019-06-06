package frontierfactions.units;

import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import frontierfactions.FrontierCore;
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
