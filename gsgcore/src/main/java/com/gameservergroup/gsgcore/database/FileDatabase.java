package com.gameservergroup.gsgcore.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public interface FileDatabase {

    File getFile();

    default void createFile() {
        Objects.requireNonNull(getFile(), "file");
        if (!getFile().exists()) {
            getFile().mkdirs();
        }
    }

    default byte[] getFileContents() throws IOException {
        Objects.requireNonNull(getFile(), "file");
        return Files.readAllBytes(getFile().toPath());
    }
}
