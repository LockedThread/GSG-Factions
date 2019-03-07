package com.gameservergroup.gsgcore.storage;

import com.gameservergroup.gsgcore.GSGCore;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class StorageFile<T> {

    private T t;
    private String name;
    private FileExtension fileExtension;
    private File file;

    public StorageFile(String name, FileExtension fileExtension) {
        this(name, GSGCore.getInstance().getDataFolder(), fileExtension);
    }

    public StorageFile(String name, File directory, FileExtension fileExtension) {
        this.name = name;
        this.fileExtension = fileExtension;
        File file = new File(directory, name + fileExtension.getExtension());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Unable to create StorageFile in " + file.toString() + ", most likely a permissions issue.", e);
            }
        }
        this.file = file;

    }

    public Optional<T> load() {
        return null;
    }

    public void save(T t) {

    }
}
