package com.gameservergroup.gsgcore.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    public static void writeToFile(File file, String input) throws IOException {
        Files.write(file.toPath(), input.getBytes(StandardCharsets.UTF_8));
    }
}
