package com.gameservergroup.gsgcore;

import com.gameservergroup.gsgcore.enums.TrenchMessages;
import com.gameservergroup.gsgcore.integration.mcmmo.McMMOIntegration;
import com.gameservergroup.gsgcore.integration.mcmmo.impl.McMMOImpl;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.units.UnitTrenchTools;

public class GSGTrenchTools extends Module {

    private static GSGTrenchTools instance;
    private McMMOIntegration mcMMOIntegration;

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

        if (getServer().getPluginManager().getPlugin("McMMO") != null) {
            this.mcMMOIntegration = new McMMOImpl();
        }

        registerUnits(new UnitTrenchTools());
    }

    @Override
    public void disable() {
        instance = null;
    }

    public McMMOIntegration getMcMMOIntegration() {
        return mcMMOIntegration;
    }
}
