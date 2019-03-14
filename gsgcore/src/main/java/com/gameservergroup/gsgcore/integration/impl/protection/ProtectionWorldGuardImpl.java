package com.gameservergroup.gsgcore.integration.impl.protection;

import com.gameservergroup.gsgcore.integration.ProtectionIntegration;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionWorldGuardImpl implements ProtectionIntegration {

    @Override
    public boolean canBuild(Player player, Location location) {
        return WorldGuardPlugin.inst().getRegionContainer().createQuery().testState(location, player, DefaultFlag.BLOCK_PLACE);
    }

    @Override
    public boolean canDestroy(Player player, Location location) {
        return WorldGuardPlugin.inst().getRegionContainer().createQuery().testState(location, player, DefaultFlag.BLOCK_BREAK);
    }
}
