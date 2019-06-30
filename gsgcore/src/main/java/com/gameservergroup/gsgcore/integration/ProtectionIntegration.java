package com.gameservergroup.gsgcore.integration;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface ProtectionIntegration {

    boolean canBuild(Player player, Location location);

    default boolean canBuild(Player player, Block block) {
        return canBuild(player, block.getLocation());
    }

    boolean canDestroy(Player player, Location location);

    default boolean canDestroy(Player player, Block block) {
        return canDestroy(player, block.getLocation());
    }

}
