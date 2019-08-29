package com.gameservergroup.gsgcore.database;

import com.gameservergroup.gsgcore.database.types.JsonDatabase;
import com.gameservergroup.gsgcore.database.types.MySQLDatabase;
import com.gameservergroup.gsgcore.database.types.RedisDatabase;
import com.gameservergroup.gsgcore.database.types.YMLDatabase;

import java.io.IOException;

public interface Database {

    @SuppressWarnings("unchecked")
    static <T extends AbstractDatabase> T create(Class<T> databaseClass) {
        if (databaseClass.equals(JsonDatabase.class)) {
            return (T) new JsonDatabase<>();
        } else if (databaseClass.equals(YMLDatabase.class)) {
            return (T) new YMLDatabase();
        } else if (databaseClass.equals(MySQLDatabase.class)) {
            return (T) new MySQLDatabase();
        } else if (databaseClass.equals(RedisDatabase.class)) {
            return (T) new RedisDatabase();
        }
        return null;
    }

    void connect() throws IOException;

    void disconnect();

    default void load() {

    }

    default void unload() {
    }


}
