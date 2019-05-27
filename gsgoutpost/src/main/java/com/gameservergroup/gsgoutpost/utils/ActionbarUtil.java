package com.gameservergroup.gsgoutpost.utils;

import com.gameservergroup.gsgcore.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ActionbarUtil {

    private static ActionbarUtil actionbarUtil;
    private Class<?> iChatBaseComponentClass, packetClass, packetPlayOutTitleClass, packetPlayOutChatClass;
    private Constructor<?> subTitleConstructor, titleConstructor;

    public ActionbarUtil() {
        try {
            this.iChatBaseComponentClass = getNMSClass("IChatBaseComponent");
            this.packetClass = getNMSClass("Packet");
            this.packetPlayOutTitleClass = getNMSClass("PacketPlayOutTitle");
            this.packetPlayOutChatClass = getNMSClass("PacketPlayOutChat");
            this.subTitleConstructor = packetPlayOutTitleClass.getDeclaredConstructor(packetPlayOutTitleClass.getDeclaredClasses()[0], iChatBaseComponentClass, int.class, int.class, int.class);
            this.titleConstructor = packetPlayOutTitleClass.getDeclaredConstructor(packetPlayOutTitleClass.getDeclaredClasses()[0], iChatBaseComponentClass, int.class, int.class, int.class);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }
    }

    public static ActionbarUtil getActionbarUtil() {
        return actionbarUtil != null ? actionbarUtil : (actionbarUtil = new ActionbarUtil());
    }

    public void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", packetClass).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void sendTitle(Player player, String title, String subtitle, int fadein, int stay, int fadeout) {
        title = Text.toColor(title);
        subtitle = Text.toColor(subtitle);
        Class<?> chatSerializer = iChatBaseComponentClass.getDeclaredClasses()[0];
        try {
            Object enumTitle = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("TITLE").get(null);
            Object chatTitle = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\": \"" + title + "\"}");
            Object packet = titleConstructor.newInstance(enumTitle, chatTitle, fadein, stay, fadeout);
            sendPacket(player, packet);
            Object enumSubtitle = packetPlayOutTitleClass.getDeclaredClasses()[0].getField("SUBTITLE").get(null);
            Object chatSubtitle = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\": \"" + subtitle + "\"}");
            packet = subTitleConstructor.newInstance(enumSubtitle, chatSubtitle, fadein, stay, fadeout);
            sendPacket(player, packet);
        } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void sendActionBar(Player player, String message) {
        message = Text.toColor(message);
        Class<?> chatSerializer = iChatBaseComponentClass.getDeclaredClasses()[0];
        try {
            Constructor<?> ConstructorActionbar = packetPlayOutChatClass.getDeclaredConstructor(iChatBaseComponentClass, byte.class);
            Object actionbar = chatSerializer.getMethod("a", String.class).invoke(chatSerializer, "{\"text\": \"" + message + "\"}");
            Object packet = ConstructorActionbar.newInstance(actionbar, (byte) 2);
            sendPacket(player, packet);
        } catch (IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
