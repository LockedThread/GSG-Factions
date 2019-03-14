package com.gameservergroup.gsgcore.integration.impl.protection;

import com.gameservergroup.gsgcore.integration.ProtectionIntegration;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ProtectionDefaultImpl implements ProtectionIntegration {

    @Override
    public boolean canBuild(Player player, Location location) {
        return true;
    }

    @Override
    public boolean canDestroy(Player player, Location location) {
        return true;
    }
}
