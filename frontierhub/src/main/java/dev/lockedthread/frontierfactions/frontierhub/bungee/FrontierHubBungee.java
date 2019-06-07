package dev.lockedthread.frontierfactions.frontierhub.bungee;

import dev.lockedthread.frontierfactions.frontierhub.bungee.objs.ServerQueue;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class FrontierHubBungee extends Plugin implements Listener {

    private static FrontierHubBungee instance;

    private RedissonClient redissonClient;
    private Configuration configuration;
    private Set<String> hubServers;
    private Map<String, ServerQueue> serverQueueMap;

    public static FrontierHubBungee getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        setupConfiguration();
        getProxy().getPluginManager().registerListener(this, this);

        Config redissonConfig = new Config().setNettyThreads(configuration.getInt("redis.netty-threads")).setThreads(configuration.getInt("redis.threads"));
        SingleServerConfig singleServerConfig = redissonConfig.useSingleServer().setAddress("redis://" + configuration.getString("redis.host") + ":" + configuration.getInt("redis.port"));
        if (configuration.getBoolean("redis.auth.enabled")) {
            singleServerConfig.setPassword(configuration.getString("redis.auth.password"));
        }
        this.redissonClient = Redisson.create(redissonConfig);
    }

    private void setupConfiguration() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");


        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("bungee" + File.separator + "config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.hubServers = configuration.getStringList("hub-servers").stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {
        if (!event.isCancelled()) {
            if (hubServers.contains(event.getTarget().getName())) {

            }
        }
    }

    @EventHandler
    public void onServerDisconnect(ServerDisconnectEvent event) {

    }

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        if (event.getState() == ServerKickEvent.State.CONNECTING || event.getState() == ServerKickEvent.State.UNKNOWN) {
            final String reason = new TextComponent(event.getKickReasonComponent()).toString();
        }
    }
}
