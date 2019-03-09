package com.gameservergroup.gsggen;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsggen.menu.GenMenu;
import com.gameservergroup.gsggen.task.TaskGeneneration;
import com.gameservergroup.gsggen.units.UnitGen;

public class GSGGen extends Module {

    private static GSGGen instance;
    private UnitGen unitGen;
    private GenMenu genMenu;

    public static GSGGen getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        saveDefaultConfig();
        registerUnits(unitGen = new UnitGen());
        this.genMenu = new GenMenu();
        new TaskGeneneration().runTaskTimerAsynchronously(this, getConfig().getLong("interval"), getConfig().getLong("interval"));
    }

    @Override
    public void disable() {
        instance = null;
    }

    public UnitGen getUnitGen() {
        return unitGen;
    }

    public GenMenu getGenMenu() {
        return genMenu;
    }
}
