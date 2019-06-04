package com.massivecraft.factions.zcore.factionboosters;

import com.massivecraft.factions.Faction;

import java.util.Map;

public interface IBooster {

    BoosterType getBoosterType();

    Map<String, Object> getMeta();

    void startBooster(Faction faction);

    void stopBooster(Faction faction);
}
