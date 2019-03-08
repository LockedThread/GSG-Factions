package com.gameservergroup.gsgcore.storage;

import com.fasterxml.jackson.jr.ob.JSON;
import com.gameservergroup.gsgcore.GSGCore;
import com.google.common.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class JsonFile<T> {

    private File directory;
    private String fileName;
    private Class<T> tClass;
    private JSON json;

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    public JsonFile(File directory, String fileName, TypeToken<T> typeToken, JSON json) {
        this.directory = directory;
        this.tClass = (Class<T>) typeToken.getRawType();
        this.fileName = fileName.endsWith(".json") ? fileName : fileName + ".json";
        this.json = json;
        System.out.println("directory=" + directory.toString());
        System.out.println("tClass=" + tClass.getName());
        System.out.println("fileName=" + fileName);
    }

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    public JsonFile(File directory, String fileName, TypeToken<T> typeToken) {
        this(directory, fileName, typeToken, GSGCore.getInstance().getJson());
    }

    public void save(T t) {
        final File file = getFile();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            Files.write(file.toPath(), json.asString(t).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<T> load() {
        if (!getFile().exists()) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(json.beanFrom(tClass, getFileContents()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public String getFileContents() throws IOException {
        return new String(Files.readAllBytes(getFile().toPath()), StandardCharsets.UTF_8);
    }

    public File getFile() {
        return new File(directory, fileName);
    }
}
