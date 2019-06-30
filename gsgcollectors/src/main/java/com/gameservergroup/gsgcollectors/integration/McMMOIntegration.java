package com.gameservergroup.gsgcollectors.integration;

import org.bukkit.entity.Player;

public interface McMMOIntegration {

    void addMcMMOExp(Player player, String type, float amount);
}
