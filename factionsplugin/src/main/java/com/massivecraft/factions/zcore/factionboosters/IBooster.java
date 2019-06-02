package com.massivecraft.factions.zcore.factionboosters;

import java.util.Map;

public interface IBooster {

    BoosterType getBoosterType();

    Map<String, Object> getMeta();

    void startBooster();

    void stopBooster();
}
