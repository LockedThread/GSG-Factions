package com.massivecraft.factions.zcore.factionboosters;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Map;

public abstract class Booster implements IBooster {

    private BoosterType boosterType;
    private Map<String, Object> metaMap;

    public Booster(ConfigurationSection section) {

    }

    public Booster(BoosterType boosterType, Map<String, Object> metaMap) {
        this.boosterType = boosterType;
        this.metaMap = metaMap;
    }

    @Override
    public BoosterType getBoosterType() {
        return boosterType;
    }

    @Override
    public Map<String, Object> getMeta() {
        return metaMap;
    }
}
