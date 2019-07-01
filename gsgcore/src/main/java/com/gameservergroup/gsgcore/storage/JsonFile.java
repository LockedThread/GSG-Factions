package com.gameservergroup.gsgcore.storage;


import com.gameservergroup.gsgcore.GSGCore;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class JsonFile<T> {

    private TypeToken<T> typeToken;
    private File file;

    public JsonFile(File directory, String fileName, TypeToken<T> typeToken) {
        if (!fileName.endsWith(".json")) {
            fileName += ".json";
        }
        this.file = new File(directory, fileName);
        this.typeToken = typeToken;
    }

    public Optional<T> load() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            return Optional.ofNullable(GSGCore.getInstance().getGson().fromJson(getFileContents(), typeToken.getType()));
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

    public File getFile() {
        return file;
    }

    public String getFileContents() {
        try {
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*try {
            return IOUtils.toString(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return null;
    }
}
