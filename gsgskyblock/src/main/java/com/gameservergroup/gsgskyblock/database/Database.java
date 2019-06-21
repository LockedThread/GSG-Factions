package com.gameservergroup.gsgskyblock.database;

import com.gameservergroup.gsgskyblock.objs.Island;
import com.gameservergroup.gsgskyblock.objs.IslandPlayer;

import java.util.HashMap;
import java.util.UUID;

public interface Database {

    void connect();

    void loadIslandData();

    void loadPlayerData();

    void disconnect();

    boolean isConnected();

    HashMap<UUID, Island> getIslandData();

    HashMap<UUID, IslandPlayer> getIslandPlayerData();

    DatabaseData getDatabaseData();
}
