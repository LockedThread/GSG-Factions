package com.gameservergroup.gsgcore.integration.impl.protection;

import com.gameservergroup.gsgcore.GSGCore;
import com.gameservergroup.gsgcore.integration.ProtectionIntegration;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProtectionFactionsUUIDImpl implements ProtectionIntegration {

    private Method playerCanBuildDestroyBlock;

    public ProtectionFactionsUUIDImpl() throws ClassNotFoundException {
        try {
            this.playerCanBuildDestroyBlock = Class.forName("com.massivecraft.factions.listeners.FactionsBlockListener",
                    false,
                    GSGCore.getInstance()
                            .getServer()
                            .getPluginManager()
                            .getPlugin("Factions")
                            .getClass()
                            .getClassLoader())
                    .getMethod("playerCanBuildDestroyBlock", Player.class, Location.class, String.class, boolean.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canBuild(Player player, Location location) {
        try {
            return (boolean) playerCanBuildDestroyBlock.invoke(null, player, location, "build", false);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean canDestroy(Player player, Location location) {
        try {
            return (boolean) playerCanBuildDestroyBlock.invoke(null, player, location, "destroy", false);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }
}
