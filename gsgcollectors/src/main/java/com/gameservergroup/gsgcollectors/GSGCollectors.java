package com.gameservergroup.gsgcollectors;

import com.gameservergroup.gsgcollectors.enums.CollectorMessages;
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
        for (CollectorMessages printerMessages : CollectorMessages.values()) {
            if (getConfig().isSet("messages." + printerMessages.getKey())) {
                printerMessages.setMessage(getConfig().getString("messages." + printerMessages.getKey()));
            } else {
                getConfig().set("messages." + printerMessages.getKey(), printerMessages.getValue());
            }
        }
        registerUnits(unitCollectors = new UnitCollectors());
    }

    @Override
    public void reload() {
        reloadConfig();

    }

    @Override
    public void disable() {
        instance = null;
    }

    public UnitCollectors getUnitCollectors() {
        return unitCollectors;
    }
}
