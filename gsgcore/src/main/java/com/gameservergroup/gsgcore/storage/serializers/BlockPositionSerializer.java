package com.gameservergroup.gsgcore.storage.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;

import java.io.IOException;

public class BlockPositionSerializer extends JsonSerializer<BlockPosition> {

    @Override
    public void serialize(BlockPosition blockPosition, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        //World
        jsonGenerator.writeFieldName("world");
        jsonGenerator.writeString(blockPosition.getWorld().getName());
        //X
        jsonGenerator.writeFieldName("x");
        jsonGenerator.writeNumber(blockPosition.getX());
        //Y
        jsonGenerator.writeFieldName("y");
        jsonGenerator.writeNumber(blockPosition.getY());
        //Z
        jsonGenerator.writeFieldName("z");
        jsonGenerator.writeNumber(blockPosition.getZ());

        jsonGenerator.writeEndObject();
        jsonGenerator.close();
    }

    @Override
    public Class<BlockPosition> handledType() {
        return BlockPosition.class;
    }
}
