package com.gameservergroup.gsgcore.utils;

import org.bukkit.Location;
import org.bukkit.WorldBorder;

public class Utils {

    public static boolean isOutsideBorder(Location location) {
        WorldBorder worldBorder = location.getWorld().getWorldBorder();
        double size = worldBorder.getSize() / 2.0;
        double centerX = worldBorder.getCenter().getX();
        double centerZ = worldBorder.getCenter().getZ();
        return centerX - size > location.getX() || centerX + size <= location.getX() || centerZ - size > location.getZ() || centerZ + size <= location.getZ();
    }
}
