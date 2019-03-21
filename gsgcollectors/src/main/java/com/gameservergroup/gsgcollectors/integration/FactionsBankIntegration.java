package com.gameservergroup.gsgcollectors.integration;

import com.massivecraft.factions.Faction;

public interface FactionsBankIntegration {

    boolean setTntBankBalance(Faction faction, int amount);

    int getTntBankBalance(Faction faction);


}
