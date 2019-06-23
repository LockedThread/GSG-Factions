package com.gameservergroup.gsgtrenchtools;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgtrenchtools.enums.TrenchMessages;
import com.gameservergroup.gsgtrenchtools.integration.coreprotect.CoreProtectIntegration;
import com.gameservergroup.gsgtrenchtools.integration.coreprotect.impl.CoreProtectImpl;
import com.gameservergroup.gsgtrenchtools.integration.mcmmo.McMMOIntegration;
import com.gameservergroup.gsgtrenchtools.integration.mcmmo.impl.McMMOImpl;
import com.gameservergroup.gsgtrenchtools.units.UnitTrenchTools;

public class GSGTrenchTools extends Module {

    private static GSGTrenchTools instance;
    private McMMOIntegration mcMMOIntegration;
    private CoreProtectIntegration coreProtectIntegration;

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
        if (getServer().getPluginManager().getPlugin("CoreProtect") != null && getConfig().getBoolean("hook-coreprotect")) {
            this.coreProtectIntegration = new CoreProtectImpl();
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

    public CoreProtectIntegration getCoreProtectIntegration() {
        return coreProtectIntegration;
    }
}
