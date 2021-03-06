package com.gameservergroup.gsgcore.storage.objs;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ChunkPosition {

    private transient final int hash;
    private String worldName;
    private int x;
    private int z;

    private ChunkPosition(String worldName, int x, int z) {
        Objects.requireNonNull(worldName, "WorldName must not be null for ChunkPosition instantiation");
        this.worldName = worldName;
        this.x = x;
        this.z = z;
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

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChunkPosition that = (ChunkPosition) o;

        return that.hash == hash;
    }

    @Override
    public int hashCode() {
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
