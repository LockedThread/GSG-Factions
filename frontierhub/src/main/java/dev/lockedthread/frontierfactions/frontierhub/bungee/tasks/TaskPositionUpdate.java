package dev.lockedthread.frontierfactions.frontierhub.bungee.tasks;

import dev.lockedthread.frontierfactions.frontierhub.bungee.FrontierHubBungee;
import dev.lockedthread.frontierfactions.frontierhub.bungee.objs.ServerQueue;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TaskPositionUpdate implements Runnable {

    @Override
    public void run() {
        for (ServerQueue serverQueue : FrontierHubBungee.getInstance().getServerQueueMap().values()) {
            for (int i = 0; i < serverQueue.getUuids().size(); i++) {
                ProxiedPlayer player = FrontierHubBungee.getInstance().getProxy().getPlayer(serverQueue.getUuids().get(i));
                player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', "&e(!) &eYour queue position is &a#" + (i + 1))));
            }
        }
    }
}
