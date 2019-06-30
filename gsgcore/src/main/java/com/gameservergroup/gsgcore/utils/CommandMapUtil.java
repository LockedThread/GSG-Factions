package com.gameservergroup.gsgcore.utils;

import com.gameservergroup.gsgcore.GSGCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;


public class CommandMapUtil {

    public static void unregisterCommand(String s) {
        getCommandMap().getCommand(s).unregister(getCommandMap());
    }

    public static void unregisterCommands(Plugin plugin) {
        final CommandMap commandMap = getCommandMap();
        getCommands().values()
                .stream()
                .filter(command -> GSGCore.getInstance().getServer().getPluginCommand(command.getName()).getPlugin().getName().equals(plugin.getName()))
                .forEach(command -> command.unregister(commandMap));
    }

    public static void registerCommand(Plugin plugin, String... aliases) {
        getCommandMap().register(plugin.getDescription().getName(), getPluginCommand(plugin, aliases));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Command> getCommands() {
        try {
            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);
            return (Map<String, org.bukkit.command.Command>) field.get(GSGCore.getInstance().getServer().getPluginManager());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("Unable to get the Bukkit KnowmCommand Map. Please contact Simpleness or LockedThread.", e);
        }
    }

    private static CommandMap getCommandMap() {
        try {
            Field field = SimplePluginManager.class.getDeclaredField("commandMap");
            field.setAccessible(true);
            return (CommandMap) field.get(GSGCore.getInstance().getServer().getPluginManager());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("Unable to get the Bukkit CommandMap. Please contact Simpleness or LockedThread.", e);
        }
    }

    private static PluginCommand getPluginCommand(Plugin plugin, String... aliases) {
        try {
            Constructor<PluginCommand> commandConstructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            commandConstructor.setAccessible(true);
            PluginCommand pluginCommand = commandConstructor.newInstance(aliases[0], plugin);
            pluginCommand.setAliases(Arrays.asList(Arrays.copyOfRange(aliases, 1, aliases.length)));
            pluginCommand.setExecutor(GSGCore.getInstance().getCommandPostExecutor());
            return pluginCommand;
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException e) {
            throw new RuntimeException("Unable to get PluginCommand. Please contact Simpleness or LockedThread.", e);
        }
    }
}