package com.gameservergroup.gsgcollectors.integration;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;

public interface FactionsBankIntegration {

    boolean setTntBankBalance(Faction faction, int amount);

    int getTntBankBalance(Faction faction);

    Faction getFaction(FPlayer fPlayer);

    Faction getFaction(Player player);

}
