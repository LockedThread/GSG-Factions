package dev.lockedthread.frontierfactions.frontierhub.bungee;

import dev.lockedthread.frontierfactions.frontierhub.bungee.commands.CommandJoinQueue;
import dev.lockedthread.frontierfactions.frontierhub.bungee.listeners.ServerListener;
import dev.lockedthread.frontierfactions.frontierhub.bungee.objs.ServerQueue;
import dev.lockedthread.frontierfactions.frontierhub.bungee.tasks.TaskPositionUpdate;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FrontierHubBungee extends Plugin {

    private static FrontierHubBungee instance;

    private Configuration configuration;
    private List<String> hubServers;
    private Map<String, ServerQueue> serverQueueMap;
    private Map<UUID, ServerQueue> playerQueueMap;
    private ThreadLocalRandom random;

    public static FrontierHubBungee getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        setupConfiguration();
        getProxy().getPluginManager().registerCommand(this, new CommandJoinQueue());

        getProxy().getPluginManager().registerListener(this, new ServerListener());

        this.playerQueueMap = new HashMap<>();
        this.random = ThreadLocalRandom.current();
        getProxy().registerChannel("FrontierHub-Return");
    }

    private void setupConfiguration() {
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("bungee/config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.hubServers = configuration.getStringList("servers.hub-servers").stream().map(String::toLowerCase).collect(Collectors.toList());
        this.serverQueueMap = new HashMap<>();
        getProxy().getScheduler().schedule(this, new TaskPositionUpdate(), configuration.getLong("position-message-update-interval"), configuration.getLong("position-message-update-interval"), TimeUnit.MILLISECONDS);

        Configuration queuedServersSection = configuration.getSection("servers.queued-servers");
        for (String key : queuedServersSection.getKeys()) {
            ServerQueue serverQueue = new ServerQueue(queuedServersSection.getString(key + ".name"));
            serverQueueMap.put(queuedServersSection.getString(key + ".name"), serverQueue);
            getProxy().getScheduler().schedule(this, serverQueue, queuedServersSection.getLong(key + ".interval"), queuedServersSection.getLong(key + ".interval"), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void onDisable() {
        instance = null;
        getProxy().getScheduler().cancel(this);
    }

    public Map<String, ServerQueue> getServerQueueMap() {
        return serverQueueMap;
    }

    public List<String> getHubServers() {
        return hubServers;
    }

    public Map<UUID, ServerQueue> getPlayerQueueMap() {
        return playerQueueMap;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ThreadLocalRandom getRandom() {
        return random;
    }
}
