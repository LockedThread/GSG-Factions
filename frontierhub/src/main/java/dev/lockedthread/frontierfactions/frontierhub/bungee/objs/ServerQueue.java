package dev.lockedthread.frontierfactions.frontierhub.bungee.objs;

import dev.lockedthread.frontierfactions.frontierhub.bungee.FrontierHubBungee;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ServerQueue implements Runnable {

    private final List<UUID> uuids;
    private final ServerInfo serverInfo;

    public ServerQueue(ServerInfo serverInfo, List<UUID> uuids) {
        this.serverInfo = serverInfo;
        this.uuids = uuids;
    }

    public ServerQueue(String serverName, List<UUID> uuids) {
        this(FrontierHubBungee.getInstance().getProxy().getServerInfo(serverName), uuids);
    }

    public ServerQueue(String serverName) {
        this(serverName, new ArrayList<>());
    }

    public void poll() {
        if (!uuids.isEmpty()) {
            serverInfo.ping((serverPing, throwable) -> {
                int online = serverPing.getPlayers().getOnline();
                int max = serverPing.getPlayers().getMax();

                if (max > online) {
                    FrontierHubBungee.getInstance().getProxy().getPlayer(uuids.get(0)).connect(serverInfo, ServerConnectEvent.Reason.PLUGIN);
                }
            });
        }
    }

    public List<UUID> getUuids() {
        return uuids;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public void run() {
        poll();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerQueue that = (ServerQueue) o;

        if (!Objects.equals(uuids, that.uuids)) return false;
        return Objects.equals(serverInfo, that.serverInfo);
    }

    @Override
    public int hashCode() {
        int result = uuids != null ? uuids.hashCode() : 0;
        result = 31 * result + (serverInfo != null ? serverInfo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ServerQueue{" +
                "uuids=" + uuids +
                ", serverInfo=" + serverInfo +
                '}';
    }
}
