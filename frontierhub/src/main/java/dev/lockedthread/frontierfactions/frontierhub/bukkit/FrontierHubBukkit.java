package dev.lockedthread.frontierfactions.frontierhub.bukkit;

import com.gameservergroup.gsgcore.plugin.Module;
import dev.lockedthread.frontierfactions.frontierhub.bukkit.units.UnitHub;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FrontierHubBukkit extends Module {

    private static FrontierHubBukkit instance;
    private File file;
    private FileConfiguration fileConfiguration;

    @Override
    public void enable() {
        instance = this;
        setupConfiguration();
        registerUnits(new UnitHub());
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void setupConfiguration() {
        if (!getDataFolder().exists())
            getDataFolder().mkdirs();
        this.file = new File(getDataFolder(), "config.yml");
        try {
            if (!file.exists()) {
                InputStream in = getResource("bukkit/config.yml");
                Files.copy(in, file.toPath());
                in.close();
            }
            this.fileConfiguration = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disable() {
        instance = null;
    }

    public static FrontierHubBukkit getInstance() {
        return instance;
    }

    @Override
    public FileConfiguration getConfig() {
        return fileConfiguration;
    }

    public File getConfigFile() {
        return file;
    }
}

