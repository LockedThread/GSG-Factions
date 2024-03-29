package com.gameservergroup.gsgcore.storage.objs;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Objects;

public class BlockPosition {

    private final String worldName;
    private final int x;
    private final int y;
    private final int z;

    public BlockPosition(String worldName, int x, int y, int z) {
        Objects.requireNonNull(worldName, "The World Name must not be null for BlockPosition instantiation");
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static BlockPosition of(Location location) {
        Objects.requireNonNull(location, "Location must not be null for BlockPosition instantiation");
        return new BlockPosition(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static BlockPosition of(Block block) {
        Objects.requireNonNull(block, "Block must not be null for BlockPosition instantiation");
        return new BlockPosition(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }

    public static BlockPosition of(World world, int x, int y, int z) {
        Objects.requireNonNull(world, "World must not be null for BlockPosition instantiation");
        return new BlockPosition(world.getName(), x, y, z);
    }

    public BlockPosition getRelative(BlockFace blockFace) {
        return new BlockPosition(worldName, x + blockFace.getModX(), y + blockFace.getModY(), z + blockFace.getModZ());
    }

    public Chunk getChunk() {
        return getWorld().getChunkAt(x >> 4, z >> 4);
    }

    public void loadChunkAsync(World.ChunkLoadCallback chunkLoadCallback) {
        getWorld().getChunkAtAsync(x >> 4, z >> 4, chunkLoadCallback);
    }

    public boolean isChunkLoaded() {
        return getWorld().isChunkLoaded(x >> 4, z >> 4);
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    public Block getBlock() {
        return getWorld().getBlockAt(x, y, z);
    }

    public Location getLocation() {
        return getBlock().getLocation();
    }

    public String getWorldName() {
        return worldName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlockPosition that = (BlockPosition) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        if (z != that.z) return false;
        return Objects.equals(worldName, that.worldName);
    }

    @Override
    public int hashCode() {
        int result = 31 + (worldName != null ? worldName.hashCode() : 0);
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "BlockPosition{" +
                "worldName='" + worldName + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
