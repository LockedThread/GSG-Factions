package com.gameservergroup.gsgcore;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.jr.ob.JSON;
import com.gameservergroup.gsgcore.commands.post.CommandPostExecutor;
import com.gameservergroup.gsgcore.menus.UnitMenu;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.units.Unit;

import java.util.HashSet;

public class GSGCore extends Module {

    private static GSGCore instance;
    private HashSet<Module> modules;
    private HashSet<Unit> units;
    private CommandPostExecutor commandPostExecutor;
    private JSON json;

    @Override
    public void enable() {
        instance = this;
        this.units = new HashSet<>();
        this.modules = new HashSet<>();
        this.commandPostExecutor = new CommandPostExecutor();
        this.json = JSON.std
                .with(JSON.Feature.WRITE_NULL_PROPERTIES).with(new JsonFactory());
        registerUnits(new UnitMenu());
    }

    @Override
    public void disable() {
        instance = null;
        getUnits()
                .stream()
                .filter(unit -> unit.getRunnable() != null)
                .forEach(unit -> unit.getRunnable().run());
    }

    public static GSGCore getInstance() {
        return instance;
    }

    public void registerModule(Module module) {
        modules.add(module);
    }

    public void unregisterModule(Module module) {
        modules.remove(module);
    }

    public CommandPostExecutor getCommandPostExecutor() {
        return commandPostExecutor;
    }

    public JSON getJson() {
        return json;
    }

    public HashSet<Unit> getUnits() {
        return units;
    }
}
