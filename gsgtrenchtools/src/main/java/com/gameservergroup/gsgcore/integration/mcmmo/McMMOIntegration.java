package com.gameservergroup.gsgcore.integration.mcmmo;

import org.bukkit.entity.Player;

public interface McMMOIntegration {

    boolean isGigaDrillEnabled(Player player);

    boolean isSuperBreakerEnabled(Player player);
}
