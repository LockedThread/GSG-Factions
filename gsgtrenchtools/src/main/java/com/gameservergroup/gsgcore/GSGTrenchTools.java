package com.gameservergroup.gsgcore;

import com.gameservergroup.gsgcore.enums.TrenchMessages;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.units.UnitTrenchTools;

public class GSGTrenchTools extends Module {

    private static GSGTrenchTools instance;

    public static GSGTrenchTools getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();
        for (TrenchMessages printerMessages : TrenchMessages.values()) {
            if (getConfig().isSet("messages." + printerMessages.getKey())) {
                printerMessages.setMessage(getConfig().getString("messages." + printerMessages.getKey()));
            } else {
                getConfig().set("messages." + printerMessages.getKey(), printerMessages.getValue());
            }
        }
        saveConfig();
        registerUnits(new UnitTrenchTools());
    }

    @Override
    public void disable() {
        instance = null;
    }
}
