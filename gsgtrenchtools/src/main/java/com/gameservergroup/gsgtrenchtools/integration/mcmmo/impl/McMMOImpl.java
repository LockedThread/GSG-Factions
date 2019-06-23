package com.gameservergroup.gsgtrenchtools.integration.mcmmo.impl;

import com.gameservergroup.gsgtrenchtools.integration.mcmmo.McMMOIntegration;
import com.gmail.nossr50.api.AbilityAPI;
import org.bukkit.entity.Player;

public class McMMOImpl implements McMMOIntegration {

    @Override
    public boolean isGigaDrillEnabled(Player player) {
        return AbilityAPI.gigaDrillBreakerEnabled(player);
    }

    @Override
    public boolean isSuperBreakerEnabled(Player player) {
        return AbilityAPI.superBreakerEnabled(player);
    }
}
