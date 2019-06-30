package com.gameservergroup.gsgcore.storage.objs;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChunkPosition {

    private transient int hash;
    private final String worldName;
    private final int x;
    private final int z;

    private ChunkPosition(String worldName, int x, int z) {
        Objects.requireNonNull(worldName, "WorldName must not be null for ChunkPosition instantiation");
        this.worldName = worldName;
        this.x = x;
        this.z = z;
        recalcHash();
    }

    public void recalcHash() {
        int result = worldName.hashCode();
        result = 31 * result + x;
        result = 31 * result + z;
        this.hash = result;
    }

    public static ChunkPosition of(World world, int x, int z) {
        Objects.requireNonNull(world, "World must not be null for ChunkPosition instantiation");
        return new ChunkPosition(world.getName(), x, z);
    }

    public static ChunkPosition of(Chunk chunk) {
        Objects.requireNonNull(chunk, "Chunk must not be null for ChunkPosition instantiation");
        return new ChunkPosition(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public Set<Block> getBlocks() {
        final Chunk chunk = getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }
        HashSet<Block> blocks = new HashSet<>();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    blocks.add(chunk.getBlock(x, y, z));
                }
            }
        }
        return blocks;
    }

    public Chunk getChunk() {
        return getWorld().getChunkAt(x, z);
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChunkPosition that = (ChunkPosition) o;
        if (that.hash == 0) {
            that.recalcHash();
        }
        if (hash == 0) {
            recalcHash();
        }
        return that.hash == hash;
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            recalcHash();
        }
        return hash;
    }

    @Override
    public String toString() {
        return "ChunkPosition{" +
                "worldName='" + worldName + '\'' +
                ", x=" + x +
                ", z=" + z +
                '}';
    }
}
