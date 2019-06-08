package dev.lockedthread.frontierfactions.frontierhub.bungee.commands;

import dev.lockedthread.frontierfactions.frontierhub.bungee.FrontierHubBungee;
import dev.lockedthread.frontierfactions.frontierhub.bungee.objs.ServerQueue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandJoinQueue extends Command {

    public CommandJoinQueue() {
        super("joinqueue");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if (commandSender instanceof ProxiedPlayer) {
            if (strings.length == 1) {
                ServerQueue serverQueue = FrontierHubBungee.getInstance().getServerQueueMap().get(strings[0].toLowerCase());
                if (serverQueue != null) {
                    ProxiedPlayer proxiedPlayer = (ProxiedPlayer) commandSender;
                    if (proxiedPlayer.getServer().getInfo().getName().equals(serverQueue.getServerInfo().getName())) {
                        commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cYou are already that server!")));
                    } else {
                        ServerQueue serverQueue1 = FrontierHubBungee.getInstance().getPlayerQueueMap().get(proxiedPlayer.getUniqueId());
                        if (serverQueue1 != null && serverQueue1.getServerInfo().getName().equals(serverQueue.getServerInfo().getName())) {
                            commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cYou are already in the queue for " + serverQueue.getServerInfo().getName())));
                        } else {
                            FrontierHubBungee.getInstance().getPlayerQueueMap().put(proxiedPlayer.getUniqueId(), serverQueue);
                            serverQueue.getUuids().add(proxiedPlayer.getUniqueId());
                            commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eYou have joined the " + serverQueue.getServerInfo().getName() + " queue")));
                        }
                    }
                } else {
                    commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cUnable to find server with name " + strings[0].toLowerCase())));
                }
            } else {
                commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&e/joinqueue [server]")));
            }
        } else if (strings.length <= 1) {
            commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&e/joinqueue [server] [player]")));
        } else if (strings.length == 2) {
            ServerQueue serverQueue = FrontierHubBungee.getInstance().getServerQueueMap().get(strings[0].toLowerCase());
            if (serverQueue != null) {
                ProxiedPlayer proxiedPlayer = FrontierHubBungee.getInstance().getProxy().getPlayer(strings[1]);
                if (proxiedPlayer != null) {
                    if (proxiedPlayer.getServer().getInfo().getName().equals(serverQueue.getServerInfo().getName())) {
                        commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c" + proxiedPlayer.getName() + " is already that server!")));
                    } else {
                        FrontierHubBungee.getInstance().getPlayerQueueMap().put(proxiedPlayer.getUniqueId(), serverQueue);
                        serverQueue.getUuids().add(proxiedPlayer.getUniqueId());
                        commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&eAdding " + proxiedPlayer.getName() + " to the queue for " + serverQueue.getServerInfo().getName())));
                    }
                }
            } else {
                commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&cUnable to find server with name " + strings[0].toLowerCase())));
            }
        } else {
            commandSender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c/joinqueue [server] [player]")));
        }
    }
}
