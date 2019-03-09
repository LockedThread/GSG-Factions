package com.gameservergroup.gsgcore.storage.objs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;

public class BlockPosition {

    private String worldName;
    private int x, y, z;

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

    private BlockPosition(String worldName, int x, int y, int z) {
        Objects.requireNonNull(worldName, "The World Name must not be null for BlockPosition instantiation");
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
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

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

        BlockPosition that = (BlockPosition) o;

        return x == that.x && y == that.y && z == that.z && Objects.equals(worldName, that.worldName);
    }

    @Override
    public int hashCode() {
        int result = worldName != null ? worldName.hashCode() : 0;
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
