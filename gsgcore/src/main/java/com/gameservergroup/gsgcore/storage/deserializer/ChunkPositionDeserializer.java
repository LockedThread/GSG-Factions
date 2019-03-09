package com.gameservergroup.gsgcore.storage.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;

public class ChunkPositionDeserializer extends JsonDeserializer<ChunkPosition> {

    @Override
    public ChunkPosition deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        int x = 0, z = 0;
        World world = null;
        while (!jsonParser.isClosed()) {
            if (jsonParser.nextToken() == JsonToken.FIELD_NAME) {
                switch (jsonParser.getCurrentName()) {
                    case "world":
                        world = Bukkit.getWorld(jsonParser.getValueAsString());
                        break;
                    case "x":
                        x = jsonParser.getValueAsInt();
                        break;
                    case "z":
                        z = jsonParser.getValueAsInt();
                        break;
                }
            }
        }
        return ChunkPosition.of(world, x, z);
    }
}
