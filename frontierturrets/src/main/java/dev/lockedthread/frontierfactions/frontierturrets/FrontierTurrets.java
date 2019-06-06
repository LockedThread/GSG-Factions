package dev.lockedthread.frontierfactions.frontierturrets;

import com.gameservergroup.gsgcore.plugin.Module;
import dev.lockedthread.frontierfactions.frontierturrets.units.UnitTurrets;

public class FrontierTurrets extends Module {

    private static FrontierTurrets instance;

    private UnitTurrets unitTurrets;

    public static FrontierTurrets getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();

        registerUnits(unitTurrets = new UnitTurrets());
    }

    @Override
    public void disable() {
        instance = null;
    }

    public UnitTurrets getUnitTurrets() {
        return unitTurrets;
    }
}
