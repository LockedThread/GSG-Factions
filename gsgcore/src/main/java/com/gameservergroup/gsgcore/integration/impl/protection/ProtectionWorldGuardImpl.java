package com.gameservergroup.gsgcore.integration.impl.protection;

import com.gameservergroup.gsgcore.integration.ProtectionIntegration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ProtectionWorldGuardImpl implements ProtectionIntegration {

    private WorldGuardPlugin worldGuardPlugin;

    public ProtectionWorldGuardImpl(Plugin plugin) {
        this.worldGuardPlugin = (WorldGuardPlugin) plugin;
    }


    @Override
    public boolean canBuild(Player player, Location location) {
        return worldGuardPlugin.canBuild(player, location);
    }

    @Override
    public boolean canDestroy(Player player, Location location) {
        return worldGuardPlugin.canBuild(player, location);
    }
}
