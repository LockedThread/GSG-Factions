package com.gameservergroup.gsgcollectors.integration.impl;

import com.gameservergroup.gsgcollectors.integration.FactionsBankIntegration;
import com.massivecraft.factions.Faction;

public class LockedThreadFactionsBankImpl implements FactionsBankIntegration {

    @Override
    public boolean setTntBankBalance(Faction faction, int amount) {
        return faction.setTntBankBalance(amount);
    }

    @Override
    public int getTntBankBalance(Faction faction) {
        return faction.getTntBankBalance();
    }
}
