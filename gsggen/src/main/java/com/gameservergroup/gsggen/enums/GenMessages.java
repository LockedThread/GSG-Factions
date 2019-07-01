package com.gameservergroup.gsggen.enums;

import org.bukkit.ChatColor;

public enum GenMessages {

    CANT_PLACE_IN_COMBAT("&cYou can't place gens in combat!"),
    CANT_AFFORD("&cYou don't have enough money to purchase this gen!"),
    ENEMIES_NEARBY("&cYou can't place gens when enemies are nearby!"),
    INVENTORY_FULL("&cUnable to give you a gen when your inventory is full!");

    private String message;

    GenMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return name().toLowerCase().replace('_', '-');
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
