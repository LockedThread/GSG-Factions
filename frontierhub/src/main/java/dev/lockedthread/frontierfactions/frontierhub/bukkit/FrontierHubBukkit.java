package dev.lockedthread.frontierfactions.frontierhub.bukkit;

import com.gameservergroup.gsgcore.plugin.Module;
import dev.lockedthread.frontierfactions.frontierhub.bukkit.units.UnitHub;

public class FrontierHubBukkit extends Module {

    private static FrontierHubBukkit instance;

    public static FrontierHubBukkit getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;

        registerUnits(new UnitHub());
    }

    @Override
    public void disable() {
        instance = null;
    }
}
