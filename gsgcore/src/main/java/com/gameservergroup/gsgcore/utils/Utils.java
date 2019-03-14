package com.gameservergroup.gsgcore.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

public class Utils {

    public static boolean isOutsideBorder(Location location) {
        WorldBorder worldBorder = location.getWorld().getWorldBorder();
        double size = worldBorder.getSize() / 2.0;
        double centerX = worldBorder.getCenter().getX();
        double centerZ = worldBorder.getCenter().getZ();
        return centerX - size > location.getX() || centerX + size <= location.getX() || centerZ - size > location.getZ() || centerZ + size <= location.getZ();
    }

    public static boolean playerInventoryIsEmpty(Player player) {
        return Arrays.stream(player.getInventory().getContents()).noneMatch(Objects::nonNull) &&
                Arrays.stream(player.getInventory().getArmorContents()).noneMatch(itemStack -> itemStack != null && itemStack.getType() != Material.AIR);
    }
}
