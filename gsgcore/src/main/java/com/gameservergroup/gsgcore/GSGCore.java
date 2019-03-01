package com.gameservergroup.gsgcore;

import org.bukkit.plugin.java.JavaPlugin;

public class GSGCore extends JavaPlugin {

    private static GSGCore instance;

    @Override
    public void onEnable() {
        instance = this;
        
    }

    @Override
    public void onDisable() {
        instance = null;

    }

    public static GSGCore getInstance() {
        return instance;
    }

}
