package com.gameservergroup.gsgcore;

import com.gameservergroup.gsgcore.commands.post.CommandPostExecutor;
import com.gameservergroup.gsgcore.plugin.Module;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public class GSGCore extends JavaPlugin {

    private static GSGCore instance;
    private HashSet<Module> modules;
    private CommandPostExecutor commandPostExecutor;

    @Override
    public void onEnable() {
        instance = this;
        this.modules = new HashSet<>();
        this.commandPostExecutor = new CommandPostExecutor();
    }

    @Override
    public void onDisable() {
        instance = null;
        //CommandMapUtil.unregisterCommands(this);
    }

    public static GSGCore getInstance() {
        return instance;
    }

    public void registerModule(Module module) {
        modules.add(module);
    }

    public void unregisterModule(Module module) {
        modules.remove(module);
        //CommandMapUtil.unregisterCommands(this);
    }

    public CommandPostExecutor getCommandPostExecutor() {
        return commandPostExecutor;
    }
}
