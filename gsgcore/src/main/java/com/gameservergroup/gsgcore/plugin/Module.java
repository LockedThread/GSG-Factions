package com.gameservergroup.gsgcore.plugin;

import com.gameservergroup.gsgcore.GSGCore;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public abstract class Module extends JavaPlugin {

    private static final GSGCore GSG_CORE = GSGCore.getInstance();

    @Override
    public void onEnable() {
        enable();
    }

    @Override
    public void onDisable() {
        disable();
    }

    public abstract void enable();

    public abstract void disable();

    @SuppressWarnings("unchecked")
    public <T extends Plugin> T getPlugin(String name, Class<T> pluginClass) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(pluginClass, "pluginClass");
        return (T) getServer().getPluginManager().getPlugin(name);
    }

    public boolean isPluginPresent(String pluginName) {
        return getServer().getPluginManager().isPluginEnabled(pluginName);
    }

    public boolean isPluginPresent(Plugin plugin) {
        return getServer().getPluginManager().isPluginEnabled(plugin);
    }
}
