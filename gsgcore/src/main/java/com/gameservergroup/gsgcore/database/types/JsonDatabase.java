package com.gameservergroup.gsgcore.database.types;

import com.gameservergroup.gsgcore.GSGCore;
import com.gameservergroup.gsgcore.database.AbstractDatabase;
import com.gameservergroup.gsgcore.database.FileDatabase;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class JsonDatabase<T> extends AbstractDatabase implements FileDatabase {

    private File file;
    private TypeToken<T> typeToken;

    public JsonDatabase() {
    }

    private JsonDatabase(JsonDatabase<T> jsonDatabase) {
        this.file = jsonDatabase.file;
        this.typeToken = jsonDatabase.typeToken;
    }

    @Override
    public void connect() {
        createFile();
    }

    @Override
    public void disconnect() {

    }

    @Override
    public File getFile() {
        return file;
    }

    public Optional<T> loadJson() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            return Optional.ofNullable(GSGCore.getInstance().getGson().fromJson(new String(getFileContents()), typeToken.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void save(T t) {
        try {
            Files.write(file.toPath(), GSGCore.getInstance().getGson().toJson(t, typeToken.getType()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JsonDatabase<T> withFile(File file) {
        this.file = file;
        return this;
    }

    public <K> JsonDatabase<K> withTypeToken(TypeToken<K> typeToken) {
        this.typeToken = (TypeToken<T>) typeToken;
        return new JsonDatabase<>((JsonDatabase<K>) this);
    }
}