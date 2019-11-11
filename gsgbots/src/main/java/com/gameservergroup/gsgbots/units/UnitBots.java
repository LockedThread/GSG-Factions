package com.gameservergroup.gsgbots.units;

import com.gameservergroup.gsgbots.GSGBots;
import com.gameservergroup.gsgbots.entities.EntityBot;
import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class UnitBots extends Unit {

    @Override
    public void setup() {
        EventPost.of(PlayerInteractAtEntityEvent.class)
                .filter(event -> event.getRightClicked() != null && event.getRightClicked() instanceof EntityBot)
                .handle(event -> {

                }).post(GSGBots.getInstance());
    }
}
