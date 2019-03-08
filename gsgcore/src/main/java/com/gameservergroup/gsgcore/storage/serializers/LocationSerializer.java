package com.gameservergroup.gsgcore.storage.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.Location;

import java.io.IOException;

public class LocationSerializer extends JsonSerializer<Location> {

    @Override
    public void serialize(Location location, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        //World
        jsonGenerator.writeFieldName("world");
        jsonGenerator.writeString(location.getWorld().getName());
        //X
        jsonGenerator.writeFieldName("x");
        jsonGenerator.writeNumber(location.getX());
        //Y
        jsonGenerator.writeFieldName("y");
        jsonGenerator.writeNumber(location.getY());
        //Z
        jsonGenerator.writeFieldName("z");
        jsonGenerator.writeNumber(location.getZ());

        jsonGenerator.writeEndObject();
        jsonGenerator.close();
    }

    @Override
    public Class<Location> handledType() {
        return Location.class;
    }
}
