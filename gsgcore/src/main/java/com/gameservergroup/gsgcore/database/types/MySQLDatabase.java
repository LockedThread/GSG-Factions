package com.gameservergroup.gsgcore.database.types;

import be.bendem.sqlstreams.SqlStream;
import com.gameservergroup.gsgcore.database.Database;
import com.gameservergroup.gsgcore.plugin.Module;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public final class MySQLDatabase extends Database {

    private static final AtomicInteger POOL_COUNTER = new AtomicInteger(0);

    private static final String DATA_SOURCE_CLASS = "org.mariadb.jdbc.MySQLDataSource";

    // https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing
    private static final int MAXIMUM_POOL_SIZE = (Runtime.getRuntime().availableProcessors() * 2) + 1;
    private static final int MINIMUM_IDLE = Math.min(MAXIMUM_POOL_SIZE, 10);

    private static final long MAX_LIFETIME = TimeUnit.MINUTES.toMillis(30); // 30 Minutes
    private static final long CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(10); // 10 seconds
    private static final long LEAK_DETECTION_THRESHOLD = TimeUnit.SECONDS.toMillis(10); // 10 seconds
    private final Module module;
    private final String databaseName, username, password;
    private HikariDataSource source;
    private SqlStream sqlStream;

    public MySQLDatabase(Module module, int port, String address, String databaseName, String username, String password) {
        super(port, address, DatabaseType.MYSQL);
        this.module = module;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean connect() throws RuntimeException {
        try {
            final HikariConfig hikari = new HikariConfig();

            hikari.setPoolName(module.getName() + "-hikari-" + POOL_COUNTER.getAndIncrement());

            hikari.setDataSourceClassName(DATA_SOURCE_CLASS);
            hikari.addDataSourceProperty("serverName", getAddress());
            hikari.addDataSourceProperty("port", getPort());
            hikari.addDataSourceProperty("databaseName", databaseName);

            hikari.setUsername(username);
            hikari.setPassword(password);

            hikari.setMaximumPoolSize(MAXIMUM_POOL_SIZE);
            hikari.setMinimumIdle(MINIMUM_IDLE);

            hikari.setMaxLifetime(MAX_LIFETIME);
            hikari.setConnectionTimeout(CONNECTION_TIMEOUT);
            hikari.setLeakDetectionThreshold(LEAK_DETECTION_THRESHOLD);

            hikari.addDataSourceProperty("properties", "useUnicode=true;characterEncoding=utf8");

            this.source = new HikariDataSource(hikari);
            this.sqlStream = SqlStream.connect(this.source);
        } catch (Exception e) {
            throw new RuntimeException("Unable to connect to SQL database", e);
        }
        return true;
    }

    public HikariDataSource getHikari() {
        return this.source;
    }

    @Override
    public void disconnect() throws Exception {
        close();
    }

    public SqlStream getSqlStream() {
        return sqlStream;
    }

    public Connection getConnection() throws SQLException {
        return Objects.requireNonNull(this.source.getConnection(), "connection is null");
    }

    public void execute(@Language("MySQL") @NotNull String statement, @NotNull Consumer<PreparedStatement> consumer) {
        try (Connection c = this.getConnection(); PreparedStatement s = c.prepareStatement(statement)) {
            consumer.accept(s);
            s.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <R> Optional<R> query(@Language("MySQL") @NotNull String query, @NotNull Consumer<PreparedStatement> consumer, @NotNull Function<ResultSet, R> function) {
        try (Connection c = this.getConnection(); PreparedStatement s = c.prepareStatement(query)) {
            consumer.accept(s);
            try (ResultSet r = s.executeQuery()) {
                return Optional.ofNullable(function.apply(r));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void close() {
        if (!this.source.isClosed()) {
            this.source.close();
        }
    }
}
