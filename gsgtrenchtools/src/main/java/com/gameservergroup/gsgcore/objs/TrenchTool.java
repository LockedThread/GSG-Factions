package com.gameservergroup.gsgcore.objs;

import com.gameservergroup.gsgcore.items.CustomItem;
import com.gameservergroup.gsgcore.items.ItemStackBuilder;
import com.gameservergroup.gsgcore.utils.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

/**
 * First try at polymorphism with CustomItems, hope all goes well.
 * <p>
 * TODO: Figure out a better way of doing this.
 */
public class TrenchTool extends CustomItem {

    private int radius;
    private boolean trayMode, omniTool;

    public TrenchTool(ConfigurationSection configurationSection, int radius, boolean trayMode, boolean omniTool) {
        super(of(configurationSection));
        this.radius = radius;
        this.trayMode = trayMode;
        this.omniTool = omniTool;
    }

    public TrenchTool(String name, ConfigurationSection configurationSection, int radius, boolean trayMode, boolean omniTool) {
        super(of(configurationSection, name));
        this.radius = radius;
        this.trayMode = trayMode;
        this.omniTool = omniTool;
    }

    // TODO: Figure out a better way of doing this, maybe a trait based system for tools.
    public ItemStack buildItemStack(boolean trayMode) {
        ItemStackBuilder itemStackBuilder = ItemStackBuilder.of(getOriginalItemStack()).setLore(getOriginalItemStack().getItemMeta().getLore()
                .stream()
                .map(s -> ChatColor.translateAlternateColorCodes('&', s.replace("{traymode}", (trayMode ? "&aEnabled" : "&cDisabled"))))
                .collect(Collectors.toList()));
        return setToolTrayMode(itemStackBuilder.build(), trayMode);
        /*
        return itemStackBuilder
                .consumeItemMeta(itemMeta -> itemMeta.setLore(itemMeta.getLore()
                        .stream()
                        .map(s -> ChatColor.translateAlternateColorCodes('&', s.replace("{traymode}", (trayMode ? "&aEnabled" : "&cDisabled"))))
                        .collect(Collectors.toList())))
                .build();*/
    }

    // Precondition: The NBTItem#getBoolean already checks for nullity
    public boolean getToolTrayMode(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        return nbtItem.getBoolean("tray-mode");
    }

    public boolean isTrayMode() {
        return trayMode;
    }

    public boolean isOmniTool() {
        return omniTool;
    }

    public int getRadius() {
        return radius;
    }

    public ItemStack setToolTrayMode(ItemStack itemStack, boolean trayMode) {
        return new NBTItem(itemStack).set("tray-mode", trayMode).buildItemStack();
    }

    public ItemStack toggleTrayMode(ItemStack itemStack) {
        NBTItem nbtItem = new NBTItem(itemStack);
        nbtItem.set("tray-mode", !nbtItem.getBoolean("tray-mode"));

        return nbtItem.buildItemStack();
    }
}
