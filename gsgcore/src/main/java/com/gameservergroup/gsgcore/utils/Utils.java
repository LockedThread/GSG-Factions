package com.gameservergroup.gsgcore.utils;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Utils {

    public static final Supplier<EnumSet<Material>> ENUM_SET_SUPPLIER = () -> EnumSet.noneOf(Material.class);
    public static final Function<String, Material> MATERIAL_PARSE_FUNCTION = s -> {
        Material material = Material.matchMaterial(s);
        if (material == null) {
            throw new RuntimeException("Unable to parse material \"" + s + "\". Please correct this material in your config!");
        }
        return material;
    };

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String s) {
        try {
            if (isInteger(s)) {
                return Integer.parseInt(s) != Math.rint(Double.parseDouble(s));
            }
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isBoolean(String s) {
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("y");
    }

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

    public static String toTitleCasing(String input) {
        input = input.toLowerCase();
        return !input.contains(" ") ? StringUtils.capitalize(input) : Arrays.stream(input.split(" ")).map(s -> StringUtils.capitalize(s) + " ").collect(Collectors.joining());
    }

    public static EnumSet<Material> parseStringListAsEnumSet(List<String> list) {
        return list.stream().map(MATERIAL_PARSE_FUNCTION).collect(Collectors.toCollection(ENUM_SET_SUPPLIER));
    }
}
