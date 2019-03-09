package com.gameservergroup.gsgcore.storage.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;

public class LocationDeserializer extends JsonDeserializer<Location> {

    @Override
    public Location deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        double x = 0.0, y = 0.0, z = 0.0;
        World world = null;

        while (!jsonParser.isClosed()) {
            if (jsonParser.nextToken() == JsonToken.FIELD_NAME) {
                switch (jsonParser.getCurrentName()) {
                    case "world":
                        world = Bukkit.getWorld(jsonParser.getValueAsString());
                        break;
                    case "x":
                        x = jsonParser.getValueAsDouble();
                        break;
                    case "y":
                        y = jsonParser.getValueAsDouble();
                        break;
                    case "z":
                        z = jsonParser.getValueAsDouble();
                        break;
                }
            }
        }
        return new Location(world, x, y, z);
    }

    @Override
    public Class<?> handledType() {
        return Location.class;
    }
}
