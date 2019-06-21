package com.gameservergroup.gsgskyblock;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgskyblock.database.Database;

public class GSGSkyBlock extends Module {

    private static GSGSkyBlock instance;

    private Database database;

    public static GSGSkyBlock get() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;

    }

    @Override
    public void disable() {

    }

    public Database getDB() {
        return database;
    }
}
