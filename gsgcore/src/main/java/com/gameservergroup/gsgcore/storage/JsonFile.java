package com.gameservergroup.gsgcore.storage;

import com.gameservergroup.gsgcore.GSGCore;
import com.gameservergroup.gsgcore.utils.Utils;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class JsonFile<T> {

    private File directory;
    private String fileName;
    private TypeToken<T> typeReference;

    public JsonFile(File directory, String fileName, TypeToken<T> typeReference) {
        this.directory = directory;
        this.typeReference = typeReference;
        this.fileName = fileName.endsWith(".json") ? fileName : fileName + ".json";
    }

    public void save(T t) {
        final File file = getFile();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            Utils.writeToFile(file, GSGCore.getInstance().getGson().toJson(t, typeReference.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Optional<T> load() {
        if (!getFile().exists()) {
            return Optional.empty();
        }
        try {

            byte[] encoded = Files.readAllBytes(getFile().toPath());
            String json = new String(encoded, StandardCharsets.UTF_8);
            if (json.isEmpty()) {
                System.out.println("json is empty, " + json);
                return Optional.empty();
            }
            return Optional.ofNullable(GSGCore.getInstance().getGson().fromJson(new InputStreamReader(Files.newInputStream(getFile().toPath()), StandardCharsets.UTF_8), typeReference.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public File getFile() {
        return new File(directory, fileName);
    }
}
