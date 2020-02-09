package com.gameservergroup.gsgcore.database;

public abstract class Database {

    private final int port;
    private final String address;
    private final DatabaseType databaseType;

    public Database(int port, String address, DatabaseType databaseType) {
        this.port = port;
        this.address = address;
        this.databaseType = databaseType;
    }

    public abstract boolean connect() throws Exception;

    public abstract void disconnect() throws Exception;

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    public enum DatabaseType {
        MYSQL(3306), SQL_LITE(3306), REDIS(6379);

        final int defaultPort;

        DatabaseType(int defaultPort) {
            this.defaultPort = defaultPort;
        }
    }
}
