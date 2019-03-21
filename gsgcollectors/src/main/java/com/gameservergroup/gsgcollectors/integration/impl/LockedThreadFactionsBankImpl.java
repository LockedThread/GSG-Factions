package com.gameservergroup.gsgcollectors.integration.impl;

import com.gameservergroup.gsgcollectors.integration.FactionsBankIntegration;
import com.massivecraft.factions.Faction;

public class LockedThreadFactionsBankImpl implements FactionsBankIntegration {

    @Override
    public void setTntBankBalance(Faction faction, int amount) {
        faction.setTntBankBalance(amount);
    }

    @Override
    public int getTntBankBalance(Faction faction) {
        return faction.getTntBankBalance();
    }
}
