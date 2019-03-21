package com.gameservergroup.gsgcrophopper;

import com.gameservergroup.gsgcore.plugin.Module;
import com.gameservergroup.gsgcore.storage.JsonFile;
import com.gameservergroup.gsgcore.storage.objs.BlockPosition;
import com.gameservergroup.gsgcore.storage.objs.ChunkPosition;
import com.gameservergroup.gsgcrophopper.units.UnitCropHopper;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;

public class GSGCropHopper extends Module {

    private static GSGCropHopper gsgCropHopper;

    private HashMap<ChunkPosition, BlockPosition> blockPositionHashMap;
    private JsonFile<HashMap<ChunkPosition, BlockPosition>> jsonFile;

    public static GSGCropHopper getInstance() {
        return gsgCropHopper;
    }

    @Override
    public void enable() {
        gsgCropHopper = this;
        this.jsonFile = new JsonFile<>(getDataFolder(), "jsonFile", new TypeToken<HashMap<ChunkPosition, BlockPosition>>() {
        });
        this.blockPositionHashMap = jsonFile.load().orElse(new HashMap<>());
        registerUnits(new UnitCropHopper());
    }

    @Override
    public void disable() {
        jsonFile.save(blockPositionHashMap);
        gsgCropHopper = null;
    }

    public void remove(Chunk chunk, boolean forceRemove) {
        BlockPosition blockPosition = blockPositionHashMap.remove(ChunkPosition.of(chunk));
        if (forceRemove) {
            blockPosition.getBlock().setType(Material.AIR);
        }
    }

    public void create(Location location) {
        blockPositionHashMap.put(ChunkPosition.of(location.getChunk()), BlockPosition.of(location));
    }

    public BlockPosition findCropHopper(Chunk chunk) {
        return blockPositionHashMap.get(ChunkPosition.of(chunk));
    }

    public HashMap<ChunkPosition, BlockPosition> getBlockPositionHashMap() {
        return blockPositionHashMap;
    }
}
