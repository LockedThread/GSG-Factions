package dev.lockedthread.frontierfactions.frontierhub.bungee.objs;

import dev.lockedthread.frontierfactions.frontierhub.bungee.FrontierHubBungee;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerQueue {

    private final ConcurrentLinkedQueue<UUID> uuids;
    private final ServerInfo serverInfo;

    public ServerQueue(ServerInfo serverInfo, ConcurrentLinkedQueue<UUID> uuids) {
        this.serverInfo = serverInfo;
        this.uuids = uuids;
    }

    public ServerQueue(String serverName, ConcurrentLinkedQueue<UUID> uuids) {
        this(FrontierHubBungee.getInstance().getProxy().getServerInfo(serverName), uuids);
    }

    public ServerQueue(String serverName) {
        this(serverName, new ConcurrentLinkedQueue<>());
    }

    public void poll() {
        if (!uuids.isEmpty()) {
            serverInfo.ping((serverPing, throwable) -> {
                int online = serverPing.getPlayers().getOnline();
                int max = serverPing.getPlayers().getMax();

                if (max > online) {
                    ProxiedPlayer player = FrontierHubBungee.getInstance().getProxy().getPlayer(uuids.poll());
                    player.connect(serverInfo, (aBoolean, throwable1) -> {
                        player.sendMessage(new TextComponent(aBoolean + " " + throwable1));
                    }, ServerConnectEvent.Reason.PLUGIN);
                } else {
                    System.out.println("Max players online");
                }
            });
        }
    }
}
