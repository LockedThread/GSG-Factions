package com.gameservergroup.gsgcore.utils;

import org.bukkit.Location;

public class Utils {

    public static boolean isInWorldBorder(Location location) {
        double length = location.getWorld().getWorldBorder().getSize() / 2;
        return Math.abs(location.getWorld().getWorldBorder().getCenter().getX() - location.getX()) < length &&
                Math.abs(location.getWorld().getWorldBorder().getCenter().getZ() - location.getZ()) < length;
    }
}
