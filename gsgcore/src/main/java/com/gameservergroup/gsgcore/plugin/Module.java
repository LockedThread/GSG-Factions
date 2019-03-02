package com.gameservergroup.gsgcore.plugin;

import com.gameservergroup.gsgcore.GSGCore;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Module extends JavaPlugin {

    private static final GSGCore GSG_CORE = GSGCore.getInstance();

    public abstract void enable();

    public abstract void disable();

    @Override
    public void onEnable() {
        enable();
        GSG_CORE.registerModule(this);
    }

    @Override
    public void onDisable() {
        disable();
        GSG_CORE.unregisterModule(this);
    }
}
