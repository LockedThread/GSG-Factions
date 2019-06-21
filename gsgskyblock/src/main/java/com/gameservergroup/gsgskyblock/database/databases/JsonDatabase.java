package com.gameservergroup.gsgskyblock.database.databases;

import com.gameservergroup.gsgcore.storage.JsonFile;
import com.gameservergroup.gsgskyblock.GSGSkyBlock;
import com.gameservergroup.gsgskyblock.database.Database;
import com.gameservergroup.gsgskyblock.database.DatabaseData;
import com.gameservergroup.gsgskyblock.database.DatabaseType;
import com.gameservergroup.gsgskyblock.exceptions.UnableToFindAnnotationException;
import com.gameservergroup.gsgskyblock.objs.Island;
import com.gameservergroup.gsgskyblock.objs.IslandPlayer;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

@DatabaseData(databaseType = DatabaseType.JSON)
public class JsonDatabase implements Database {

    private JsonFile<HashMap<UUID, Island>> islandJsonFile;
    private JsonFile<HashMap<UUID, IslandPlayer>> islandPlayerJsonFile;

    private HashMap<UUID, Island> islandMap;
    private HashMap<UUID, IslandPlayer> islandPlayerMap;
    private boolean connected;
    private DatabaseData databaseData;

    @Override
    public void connect() {
        File jsonDatabaseDirectory = new File(GSGSkyBlock.get().getDataFolder(), "databases");
        if (!jsonDatabaseDirectory.exists()) {
            jsonDatabaseDirectory.mkdir();
        }
        this.islandMap = (this.islandJsonFile = new JsonFile<>(jsonDatabaseDirectory, "islands", new TypeToken<>())).load().orElse(new HashMap<>());
        this.islandPlayerMap = (this.islandPlayerJsonFile = new JsonFile<>(jsonDatabaseDirectory, "players", new TypeToken<>())).load().orElse(new HashMap<>());
        this.connected = true;
    }

    @Override
    public void loadIslandData() {

    }

    @Override
    public void loadPlayerData() {

    }

    @Override
    public void disconnect() {
        this.connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public HashMap<UUID, Island> getIslandData() {
        return islandMap;
    }

    @Override
    public HashMap<UUID, IslandPlayer> getIslandPlayerData() {
        return islandPlayerMap;
    }

    @Override
    public DatabaseData getDatabaseData() {
        if (databaseData != null || (databaseData = getClass().getDeclaredAnnotation(DatabaseData.class)) != null) {
            return databaseData;
        }
        throw new UnableToFindAnnotationException("Unable to find DatabaseData annotation. Contact LockedThread#5691 on discord");
    }
}
