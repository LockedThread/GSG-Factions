package com.massivecraft.factions.units;

import com.gameservergroup.gsgcore.events.EventPost;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgcore.utils.Utils;
import com.massivecraft.factions.P;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class UnitWorldBorder extends Unit {

    @Override
    public void setup() {
        if (P.p.getConfig().getBoolean("world-border.disable-enderpearl")) {
            EventPost.of(PlayerTeleportEvent.class)
                    .filter(event -> Utils.isOutsideBorder(event.getTo()))
                    .handle(event -> event.setFrom(event.getTo()))
                    .post(P.p);
        }
        if (P.p.getConfig().getBoolean("world-border.disable-tnt")) {
            EventPost.of(BlockExplodeEvent.class)
                    .handle(event -> event.blockList().removeIf(block -> Utils.isOutsideBorder(block.getLocation()))).post(P.p);
        }
        if (P.p.getConfig().getBoolean("world-border.disable-liquid")) {
            EventPost.of(BlockFromToEvent.class)
                    .filter(event -> Utils.isOutsideBorder(event.getToBlock().getLocation()))
                    .handle(event -> event.setCancelled(true))
                    .post(P.p);
        }
    }
}
