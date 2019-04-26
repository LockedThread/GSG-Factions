package com.massivecraft.factions.zcore.factionupgrades;

import com.massivecraft.factions.Faction;

public interface FactionUpgradeUpdate {

    void update(Faction faction, int newLevel);
}
