package com.gameservergroup.gsgcore.plugin;

import com.gameservergroup.gsgcore.units.Unit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Module extends JavaPlugin {

    public abstract void enable();

    public abstract void disable();

    @Override
    public void onEnable() {
        enable();
    }

    @Override
    public void onDisable() {
        disable();
    }

    public void registerUnits(Unit... units) {
        for (Unit unit : units) {
            unit.call();
        }
    }
}
