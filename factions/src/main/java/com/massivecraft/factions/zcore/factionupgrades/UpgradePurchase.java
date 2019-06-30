package com.massivecraft.factions.zcore.factionupgrades;

import org.bukkit.entity.Player;

public interface UpgradePurchase {

    boolean purchase(Player player, double cost);

    default boolean purchase(Player player, int cost) {
        return purchase(player, (double) cost);
    }
}
