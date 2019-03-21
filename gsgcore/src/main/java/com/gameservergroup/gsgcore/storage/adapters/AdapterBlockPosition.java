package com.gameservergroup.gsgcore.storage.adapters;

import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;

public class AdapterBlockPosition extends TypeAdapter<BlockPosition> {

    @Override
    public void write(JsonWriter jsonWriter, BlockPosition blockPosition) throws IOException {
        jsonWriter.beginObject();

        jsonWriter.name("world");
        jsonWriter.value(blockPosition.getWorld().getName());

        jsonWriter.name("x");
        jsonWriter.value(blockPosition.getX());

        jsonWriter.name("y");
        jsonWriter.value(blockPosition.getY());

        jsonWriter.name("z");
        jsonWriter.value(blockPosition.getZ());

        jsonWriter.endObject();
    }

    @Override
    public BlockPosition read(JsonReader jsonReader) throws IOException {
        int x = 0, y = 0, z = 0;
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
                    case "y":
                        y = jsonReader.nextInt();
                        break;
                    case "z":
                        z = jsonReader.nextInt();
                        break;
                }
            }
        }
        jsonReader.endObject();
        return BlockPosition.of(world, x, y, z);
    }
}
