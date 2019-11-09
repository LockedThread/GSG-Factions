package com.gameservergroup.gsgcore.integration.coreprotect.impl;

import com.gameservergroup.gsgcore.integration.coreprotect.CoreProtectIntegration;
import net.coreprotect.CoreProtect;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CoreProtectImpl implements CoreProtectIntegration {

    @Override
    public void log(Player player, Block block) {
        CoreProtect.getInstance().getAPI().logRemoval(player.getName(), block.getLocation(), block.getType(), block.getData());
    }
}
