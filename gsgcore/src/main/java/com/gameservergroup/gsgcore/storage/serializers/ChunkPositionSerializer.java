package com.gameservergroup.gsgcore.storage.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;

import java.io.IOException;

public class ChunkPositionSerializer extends JsonSerializer<ChunkPosition> {

    @Override
    public void serialize(ChunkPosition chunkPosition, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        //World
        jsonGenerator.writeFieldName("world");
        jsonGenerator.writeString(chunkPosition.getWorld().getName());
        //X
        jsonGenerator.writeFieldName("x");
        jsonGenerator.writeNumber(chunkPosition.getX());
        //Z
        jsonGenerator.writeFieldName("z");
        jsonGenerator.writeNumber(chunkPosition.getZ());

        jsonGenerator.writeEndObject();
        jsonGenerator.close();
    }

    @Override
    public Class<ChunkPosition> handledType() {
        return ChunkPosition.class;
    }
}
