package com.gameservergroup.gsgcollectors.integration;

import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;

public interface SellPriceModifier {

    double getModifiedPrice(Faction faction, double regularSellPrice);

    double getModifiedPrice(Player player, double regularSellPrice);
}
