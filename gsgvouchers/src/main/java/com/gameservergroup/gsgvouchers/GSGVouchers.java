package com.gameservergroup.gsgvouchers;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgvouchers.units.UnitVouchers;

public class GSGVouchers extends Module {

    private static GSGVouchers instance;

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();
        registerUnits(new UnitVouchers());
    }

    @Override
    public void disable() {

    }

    public static GSGVouchers getInstance() {
        return instance;
    }
}
