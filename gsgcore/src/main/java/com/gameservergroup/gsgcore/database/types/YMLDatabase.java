package com.gameservergroup.gsgcore.database.types;

import com.gameservergroup.gsgcore.database.AbstractDatabase;
import com.gameservergroup.gsgcore.database.FileDatabase;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class YMLDatabase extends AbstractDatabase implements FileDatabase {

    private File file;
    private YamlConfiguration yamlConfiguration;

    public YMLDatabase withFile(File file) {
        this.file = file;
        return this;
    }

    @Override
    public void connect() {
        createFile();
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void disconnect() {

    }

    @Override
    public File getFile() {
        return file;
    }

    public YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration;
    }
}
