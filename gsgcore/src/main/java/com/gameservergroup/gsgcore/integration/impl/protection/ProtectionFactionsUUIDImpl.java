package com.gameservergroup.gsgcore.integration.impl.protection;

import com.gameservergroup.gsgcore.integration.ProtectionIntegration;
import com.massivecraft.factions.listeners.FactionsBlockListener;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionFactionsUUIDImpl implements ProtectionIntegration {

    @Override
    public boolean canBuild(Player player, Location location) {
        return FactionsBlockListener.playerCanBuildDestroyBlock(player, location, "build", true);
    }

    @Override
    public boolean canDestroy(Player player, Location location) {
        return FactionsBlockListener.playerCanBuildDestroyBlock(player, location, "destroy", true);
    }
}
