package dev.lockedthread.frontierfactions.frontierhub.enums;

import org.bukkit.ChatColor;

public enum Messages {

    BUNGEE_SERVER_POSITION("&eYou are now in position &f{position}"),
    BUNGEE_SERVER_SEND("&eYou are now being sent to {message}");

    private String message;

    Messages(String message) {
        this.message = message;
    }

    public String getKey() {
        return name().toLowerCase().replace("_", "-");
    }

    public String getValue() {
        return message;
    }

    @Override
    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
