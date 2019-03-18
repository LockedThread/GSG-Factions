package com.gameservergroup.gsgcore.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gameservergroup.gsgcore.GSGCore;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class JsonFile<T> {

    private File directory;
    private String fileName;
    private TypeReference<T> typeReference;
    private ObjectMapper objectMapper;

    public JsonFile(File directory, String fileName, TypeReference<T> typeReference, ObjectMapper objectMapper) {
        this.directory = directory;
        this.typeReference = typeReference;
        this.fileName = fileName.endsWith(".json") ? fileName : fileName + ".json";
        this.objectMapper = objectMapper;
    }

    public JsonFile(File directory, String fileName, TypeReference<T> typeReference) {
        this(directory, fileName, typeReference, GSGCore.getInstance().getJsonObjectMapper());
    }

    public void save(T t) {
        final File file = getFile();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            objectMapper.writeValue(file, t);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<T> load() {
        if (!getFile().exists()) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(objectMapper.readValue(getFile(), typeReference));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public File getFile() {
        return new File(directory, fileName);
    }
}
