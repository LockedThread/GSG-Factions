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
            JsonToken jsonToken = jsonParser.nextToken();
            if (jsonToken == JsonToken.FIELD_NAME) {
                String fieldName = jsonParser.getCurrentName();
                System.out.println("fieldName=" + fieldName);
                switch (fieldName) {
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
        Location location = new Location(world, x, y, z);
        System.out.println(location.toString());
        return location;
    }

    @Override
    public Class<?> handledType() {
        return Location.class;
    }
}
