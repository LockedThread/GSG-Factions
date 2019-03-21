package com.gameservergroup.gsgcollectors;

import com.gameservergroup.gsgcollectors.units.UnitCollectors;
import com.gameservergroup.gsgcore.plugin.Module;

public class GSGCollectors extends Module {

    private static GSGCollectors instance;
    private UnitCollectors unitCollectors;

    public static GSGCollectors getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();

        registerUnits(unitCollectors = new UnitCollectors());
    }

    @Override
    public void disable() {
        instance = null;
    }

    public UnitCollectors getUnitCollectors() {
        return unitCollectors;
    }
}
