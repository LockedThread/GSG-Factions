package com.gameservergroup.gsgcore.items;

import com.gameservergroup.gsgcore.utils.NBTItem;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;

public class CustomItem {

    private static HashMap<String, CustomItem> customItems = new HashMap<>();
    private String name;
    private ItemStack itemStack;

    private CustomItem(String name, ItemStack itemStack) {
        this.name = name;
        this.itemStack = itemStack;

        customItems.put(name, this);

    }

    public static CustomItem of(ConfigurationSection section, String name) {
        final ItemStackBuilder itemStackBuilder = ItemStackBuilder.of(Material.matchMaterial(section.getString("material")));
        if (section.isSet("amount")) {
            if (section.isInt("amount")) {
                itemStackBuilder.setAmount(section.getInt("amount"));
            } else {
                throw new RuntimeException(section.getCurrentPath() + ".amount can't be parsed as an integer");
            }
        }
        if (section.isSet("data")) {
            if (section.isString("data")) {
                final DyeColor dyeColor = parseDyeColor(section.getString("data"));
                if (dyeColor != null) {
                    itemStackBuilder.setDyeColor(dyeColor);
                } else {
                    itemStackBuilder.setData(Short.parseShort(section.getString("data")));
                }
            } else {
                itemStackBuilder.setData((short) section.getInt("data"));
            }
        }
        if (section.isSet("color")) {
            if (section.isColor("color")) {
                itemStackBuilder.setColor(section.getColor("color"));
            } else {
                throw new RuntimeException(section.getCurrentPath() + ".color can't be parsed as a org.bukkit.Color");
            }
        }
        if (section.isSet("name") && section.isString("name")) {
            itemStackBuilder.setDisplayName(section.getString("name"));
        }
        if (section.isSet("lore") && section.isList("lore")) {
            itemStackBuilder.setLore(section.getStringList("lore"));
        }
        if (section.isSet("itemflags") && section.isList("itemflags")) {
            itemStackBuilder.addItemFlags(section.getStringList("itemflags").stream().map(CustomItem::parseItemFlag).toArray(ItemFlag[]::new));
        }
        return of(itemStackBuilder, name);
    }

    public static CustomItem of(ConfigurationSection configurationSection) {
        return of(configurationSection, configurationSection.getName());
    }

    public static CustomItem of(ItemStackBuilder itemStackBuilder, String name) {
        return new CustomItem(name, new NBTItem(itemStackBuilder.build()).set(name, true).buildItemStack());
    }

    private static DyeColor parseDyeColor(String s) {
        try {
            return DyeColor.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to parse " + s + " as a Bukkit DyeColor", e);
        }
    }

    private static ItemFlag parseItemFlag(String s) {
        try {
            return ItemFlag.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unable to parse " + s + " as a Bukkit DyeColor", e);
        }
    }

    public static HashMap<String, CustomItem> getCustomItems() {
        return customItems;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getName() {
        return name;
    }

    public static CustomItem findCustomItem(ItemStack itemStack) {
        return new NBTItem(itemStack).getKeys()
                .stream()
                .map(key -> customItems.get(key))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    public static CustomItem getCustomItem(String name) {
        return customItems.get(name);
    }
}
