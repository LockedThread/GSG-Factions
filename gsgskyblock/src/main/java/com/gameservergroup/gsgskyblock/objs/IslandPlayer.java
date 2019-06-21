package com.gameservergroup.gsgskyblock.objs;

import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgskyblock.enums.IslandRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class IslandPlayer {

    private final UUID playerUUID;
    private final long firstJoinTime;
    private UUID islandUUID;
    private IslandRole islandRole;
    private long lastJoinTime;
    private BlockPosition logoutLocation;

    private transient Player player;
    private transient Island island;

    public IslandPlayer(Player player) {
        this.playerUUID = player.getUniqueId();
        this.player = player;
        this.firstJoinTime = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player != null ? player : (player = Bukkit.getPlayer(playerUUID));
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Island getIsland() {
        return island;
    }

    public void setIsland(Island island) {
        this.island = island;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public UUID getIslandUUID() {
        return islandUUID;
    }

    public void setIslandUUID(UUID islandUUID) {
        this.islandUUID = islandUUID;
    }

    public IslandRole getIslandRole() {
        return islandRole;
    }

    public void setIslandRole(IslandRole islandRole) {
        this.islandRole = islandRole;
    }

    public long getFirstJoinTime() {
        return firstJoinTime;
    }

    public long getLastJoinTime() {
        return lastJoinTime;
    }

    public void setLastJoinTime(long lastJoinTime) {
        this.lastJoinTime = lastJoinTime;
    }

    public BlockPosition getLogoutLocation() {
        return logoutLocation;
    }

    public void setLogoutLocation(BlockPosition logoutLocation) {
        this.logoutLocation = logoutLocation;
    }

    @Override
    public String toString() {
        return "IslandPlayer{" +
                "playerUUID=" + playerUUID +
                ", islandUUID=" + islandUUID +
                ", islandRole=" + islandRole +
                ", firstJoinTime=" + firstJoinTime +
                ", lastJoinTime=" + lastJoinTime +
                ", logoutLocation=" + logoutLocation +
                ", player=" + player +
                ", island=" + island +
                '}';
    }
}
