package com.gameservergroup.gsgcore.utils;

import org.bukkit.ChatColor;

public class Text {

    public static String toColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
