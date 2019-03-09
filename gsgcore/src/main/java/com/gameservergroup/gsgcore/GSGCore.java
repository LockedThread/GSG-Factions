package com.gameservergroup.gsgcore;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.gameservergroup.gsgcore.commands.post.CommandPostExecutor;
import com.gameservergroup.gsgcore.menus.UnitMenu;
import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.storage.deserializer.BlockPositionDeserializer;
import com.gameservergroup.gsgcore.storage.deserializer.ChunkPositionDeserializer;
import com.gameservergroup.gsgcore.storage.deserializer.LocationDeserializer;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;
import com.gameservergroup.gsgcore.storage.serializers.BlockPositionSerializer;
import com.gameservergroup.gsgcore.storage.serializers.ChunkPositionSerializer;
import com.gameservergroup.gsgcore.storage.serializers.LocationSerializer;
import com.gameservergroup.gsgcore.units.Unit;
import com.gameservergroup.gsgcore.units.UnitTest;
import org.bukkit.Location;

import java.util.HashSet;

public class GSGCore extends Module {

    private static GSGCore instance;
    private HashSet<Module> modules;
    private HashSet<Unit> units;
    private CommandPostExecutor commandPostExecutor;
    private ObjectMapper jsonObjectMapper;

    public static GSGCore getInstance() {
        return instance;
    }

    @Override
    public void enable() {
        instance = this;
        setupJson();
        this.units = new HashSet<>();
        this.modules = new HashSet<>();
        this.commandPostExecutor = new CommandPostExecutor();
        registerUnits(new UnitMenu(), new UnitTest());
    }

    @Override
    public void disable() {
        instance = null;
        getUnits()
                .stream()
                .filter(unit -> unit.getRunnable() != null)
                .forEach(unit -> unit.getRunnable().run());
    }

    private void setupJson() {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        SimpleModule serializers = new SimpleModule("Serializers", new Version(1, 0, 0, null));

        //Location
        serializers.addSerializer(new LocationSerializer()).addDeserializer(Location.class, new LocationDeserializer());
        //BlockPosition
        serializers.addSerializer(new BlockPositionSerializer()).addDeserializer(BlockPosition.class, new BlockPositionDeserializer());
        //ChunkPosition
        serializers.addSerializer(new ChunkPositionSerializer()).addDeserializer(ChunkPosition.class, new ChunkPositionDeserializer());
        objectMapper.registerModule(serializers);

        this.jsonObjectMapper = objectMapper;
    }

    public void registerModule(Module module) {
        modules.add(module);
    }

    public void unregisterModule(Module module) {
        modules.remove(module);
    }

    public CommandPostExecutor getCommandPostExecutor() {
        return commandPostExecutor;
    }

    public HashSet<Unit> getUnits() {
        return units;
    }

    public ObjectMapper getJsonObjectMapper() {
        return jsonObjectMapper;
    }
}
