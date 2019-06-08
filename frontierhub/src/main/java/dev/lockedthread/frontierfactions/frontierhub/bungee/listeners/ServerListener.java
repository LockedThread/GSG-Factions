package dev.lockedthread.frontierfactions.frontierhub.bungee.listeners;

import dev.lockedthread.frontierfactions.frontierhub.bungee.FrontierHubBungee;
import dev.lockedthread.frontierfactions.frontierhub.bungee.objs.ServerQueue;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

public class ServerListener implements Listener {

    private static final FrontierHubBungee FRONTIER_HUB_BUNGEE = FrontierHubBungee.getInstance();

    @EventHandler
    public void onServerDisconnect(ServerDisconnectEvent event) {
        System.out.println("event = [" + event + "]");
        System.out.println("ServerListener.onServerDisconnect 0");
        if (FRONTIER_HUB_BUNGEE.getHubServers().contains(event.getTarget().getName())) {
            System.out.println("ServerListener.onServerDisconnect 1");
            ProxiedPlayer proxiedPlayer = event.getPlayer();
            ServerQueue serverQueue = FRONTIER_HUB_BUNGEE.getPlayerQueueMap().get(proxiedPlayer.getUniqueId());
            if (serverQueue != null) {
                System.out.println("ServerListener.onServerDisconnect 2");
                FRONTIER_HUB_BUNGEE.getPlayerQueueMap().remove(proxiedPlayer.getUniqueId());
                serverQueue.getUuids().remove(proxiedPlayer.getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerConnect(ServerConnectEvent event) {
        System.out.println("event = [" + event + "]");
        System.out.println("ServerListener.onServerConnect 0");
        if (event.getReason() == ServerConnectEvent.Reason.PLUGIN && !event.isCancelled()) {
            System.out.println("ServerListener.onServerConnect 1");
            ServerQueue serverQueue = FRONTIER_HUB_BUNGEE.getPlayerQueueMap().get(event.getPlayer().getUniqueId());
            if (serverQueue != null) {
                System.out.println("ServerListener.onServerConnect 2");
                serverQueue.getUuids().remove(event.getPlayer().getUniqueId());
                FRONTIER_HUB_BUNGEE.getPlayerQueueMap().remove(event.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerKick(ServerKickEvent event) {
        System.out.println(0);
        if (event.getState() == ServerKickEvent.State.CONNECTING) {
            System.out.println(1);
            ServerQueue serverQueue = FrontierHubBungee.getInstance().getPlayerQueueMap().get(event.getPlayer().getUniqueId());
            if (serverQueue != null) {
                System.out.println(2);
                final String reason = new TextComponent(event.getKickReasonComponent()).toString().toLowerCase();
                for (String s : FRONTIER_HUB_BUNGEE.getConfiguration().getStringList("blacklisted-kick-messages")) {
                    System.out.println(3);
                    if (reason.contains(s.toLowerCase())) {
                        System.out.println(4);
                        serverQueue.getUuids().set(0, event.getPlayer().getUniqueId());
                        //noinspection ConstantConditions
                        event.setKickReason(null);
                        break;
                    }
                }
            }
        } else {
            System.out.println("event.getState() = " + event.getState());
            List<String> hubServers = FRONTIER_HUB_BUNGEE.getHubServers();
            System.out.println("hubServers.size() = " + hubServers.size());
            if (!hubServers.isEmpty()) {
                final String reason = new TextComponent(event.getKickReasonComponent()).toString().toLowerCase();
                System.out.println("reason = " + reason);
                for (String s : FRONTIER_HUB_BUNGEE.getConfiguration().getStringList("restart-message")) {
                    if (reason.contains(s.toLowerCase())) {
                        String serverName = hubServers.get(FRONTIER_HUB_BUNGEE.getRandom().nextInt(hubServers.size()));
                        System.out.println("serverName = " + serverName);
                        event.getPlayer().connect(FRONTIER_HUB_BUNGEE.getProxy().getServerInfo(serverName), ServerConnectEvent.Reason.SERVER_DOWN_REDIRECT);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String channel = in.readUTF();
                if (channel.equals("frontier-hub-commands")) {
                    String message = in.readUTF();
                    String[] strings = message.split(" ");
                    FRONTIER_HUB_BUNGEE.getProxy().getPluginManager().dispatchCommand(FRONTIER_HUB_BUNGEE.getProxy().getPlayer(strings[0]), "joinqueue " + strings[1]);
                }
            } catch (IOException ignored) {
            }
        }
    }
}
