package com.gameservergroup.gsgcore.storage;


import com.gameservergroup.gsgcore.GSGCore;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;

import java.io.*;
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
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(GSGCore.getInstance().getGson().toJson(t, typeToken.getType()));
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getFile() {
        return file;
    }

    public TypeToken<T> getTypeToken() {
        return typeToken;
    }

    public String getFileContents() {
        try {
            return IOUtils.toString(new FileReader(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
