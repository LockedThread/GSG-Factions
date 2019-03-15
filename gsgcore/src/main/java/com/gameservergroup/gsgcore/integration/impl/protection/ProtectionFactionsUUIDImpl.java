package com.gameservergroup.gsgcore.integration.impl.protection;

import com.gameservergroup.gsgcore.GSGCore;
import com.gameservergroup.gsgcore.integration.ProtectionIntegration;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class ProtectionFactionsUUIDImpl implements ProtectionIntegration {

    @Override
    public boolean canBuild(Player player, Location location) {
        try {
            return (boolean) Class.forName("com.massivecraft.factions.listeners.FactionsBlockListener",
                    false,
                    GSGCore.getInstance()
                            .getServer()
                            .getPluginManager()
                            .getPlugin("Factions")
                            .getClass()
                            .getClassLoader())
                    .getMethod("playerCanBuildDestroyBlock", Player.class, Location.class, String.class, boolean.class)
                    .invoke(null, player, location, "build", false);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean canDestroy(Player player, Location location) {
        try {
            return (boolean) Class.forName("com.massivecraft.factions.listeners.FactionsBlockListener",
                    false,
                    GSGCore.getInstance()
                            .getServer()
                            .getPluginManager()
                            .getPlugin("Factions")
                            .getClass()
                            .getClassLoader())
                    .getMethod("playerCanBuildDestroyBlock", Player.class, Location.class, String.class, boolean.class)
                    .invoke(null, player, location, "destroy", false);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
