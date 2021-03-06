package com.gameservergroup.gsgcollectors.integration.impl;

import com.gameservergroup.gsgcollectors.integration.FactionsBankIntegration;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;

public class LockedThreadFactionsBankImpl implements FactionsBankIntegration {

    @Override
    public boolean setTntBankBalance(Faction faction, int amount) {
        return faction.setTntBankBalance(amount);
    }

    @Override
    public int getTntBankBalance(Faction faction) {
        return faction.getTntBankBalance();
    }

    @Override
    public Faction getFaction(FPlayer fPlayer) {
        return fPlayer.getFaction();
    }

    @Override
    public Faction getFaction(Player player) {
        return getFaction(FPlayers.getInstance().getByPlayer(player));
    }
}
