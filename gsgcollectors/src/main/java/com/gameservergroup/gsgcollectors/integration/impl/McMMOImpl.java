package com.gameservergroup.gsgcollectors.integration.impl;

import com.gameservergroup.gsgcollectors.integration.McMMOIntegration;
import com.gmail.nossr50.api.ExperienceAPI;
import org.bukkit.entity.Player;

public class McMMOImpl implements McMMOIntegration {

    @Override
    public void addMcMMOExp(Player player, String type, float amount) {
        ExperienceAPI.addRawXP(player, type, amount);
    }
}
