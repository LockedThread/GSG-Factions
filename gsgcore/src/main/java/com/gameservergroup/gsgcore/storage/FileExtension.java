package com.gameservergroup.gsgcore.storage;

public enum FileExtension {

    YML(".yml"),
    JSON(".json"),
    CSV(".csv");

    private String extension;

    FileExtension(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
