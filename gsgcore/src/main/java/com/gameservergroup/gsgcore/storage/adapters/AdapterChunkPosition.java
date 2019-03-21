package com.gameservergroup.gsgcore.storage.adapters;

import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;

public class AdapterChunkPosition extends TypeAdapter<ChunkPosition> {

    @Override
    public void write(JsonWriter jsonWriter, ChunkPosition chunkPosition) throws IOException {
        jsonWriter.beginObject();

        jsonWriter.name("world");
        jsonWriter.value(chunkPosition.getWorldName());

        jsonWriter.name("x");
        jsonWriter.value(chunkPosition.getX());

        jsonWriter.name("z");
        jsonWriter.value(chunkPosition.getZ());

        jsonWriter.endObject();
    }

    @Override
    public ChunkPosition read(JsonReader jsonReader) throws IOException {
        int x = 0, z = 0;
        World world = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            if (jsonReader.peek() != JsonToken.NULL) {
                switch (jsonReader.nextName()) {
                    case "world":
                        world = Bukkit.getWorld(jsonReader.nextString());
                        break;
                    case "x":
                        x = jsonReader.nextInt();
                        break;
                    case "z":
                        z = jsonReader.nextInt();
                        break;
                }
            }
        }
        jsonReader.endObject();
        return ChunkPosition.of(world, x, z);
    }
}
