package com.massivecraft.factions;

import com.massivecraft.factions.util.MiscUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class FLocation implements Serializable {

    private static final long serialVersionUID = -8292915234027387983L;
    private static final boolean worldBorderSupport;

    static {
        boolean worldBorderClassPresent = false;
        try {
            Class.forName("org.bukkit.WorldBorder");
            worldBorderClassPresent = true;
        } catch (ClassNotFoundException ignored) {
        }

        worldBorderSupport = worldBorderClassPresent;
    }

    private String worldName = "world";
    private int x = 0;
    private int z = 0;

    private transient int hash;

    //----------------------------------------------//
    // Constructors
    //----------------------------------------------//

    public FLocation() {

    }

    public FLocation(String worldName, int x, int z) {
        this.worldName = worldName;
        this.x = x;
        this.z = z;
    }

    public FLocation(Location location) {
        this(location.getWorld().getName(), blockToChunk(location.getBlockX()), blockToChunk(location.getBlockZ()));
    }

    public FLocation(Player player) {
        this(player.getLocation());
    }

    public FLocation(FPlayer fplayer) {
        this(fplayer.getPlayer());
    }

    public FLocation(Block block) {
        this(block.getLocation());
    }

    //----------------------------------------------//
    // Getters and Setters
    //----------------------------------------------//

    public static FLocation fromString(String string) {
        int index = string.indexOf(",");
        int start = 1;
        String worldName = string.substring(start, index);
        start = index + 1;
        index = string.indexOf(",", start);
        int x = Integer.valueOf(string.substring(start, index));
        int y = Integer.valueOf(string.substring(index + 1, string.length() - 1));
        return new FLocation(worldName, x, y);
    }

    // bit-shifting is used because it's much faster than standard division and multiplication
    public static int blockToChunk(int blockVal) {    // 1 chunk is 16x16 blocks
        return blockVal >> 4;   // ">> 4" == "/ 16"
    }

    public static int blockToRegion(int blockVal) {    // 1 region is 512x512 blocks
        return blockVal >> 9;   // ">> 9" == "/ 512"
    }

    public static int chunkToRegion(int chunkVal) {    // 1 region is 32x32 chunks
        return chunkVal >> 5;   // ">> 5" == "/ 32"
    }

    public static int chunkToBlock(int chunkVal) {
        return chunkVal << 4;   // "<< 4" == "* 16"
    }

    public static int regionToBlock(int regionVal) {
        return regionVal << 9;   // "<< 9" == "* 512"
    }

    public static int regionToChunk(int regionVal) {
        return regionVal << 5;   // "<< 5" == "* 32"
    }

    public static HashSet<FLocation> getArea(FLocation from, FLocation to) {
        HashSet<FLocation> ret = new HashSet<>();

        for (long x : MiscUtil.range(from.getX(), to.getX())) {
            for (long z : MiscUtil.range(from.getZ(), to.getZ())) {
                ret.add(new FLocation(from.getWorldName(), (int) x, (int) z));
            }
        }

        return ret;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    //----------------------------------------------//
    // Block/Chunk/Region Value Transformation
    //----------------------------------------------//

    public long getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public long getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getCoordString() {
        return "" + x + "," + z;
    }

    public Chunk getChunk() {
        return new Location(getWorld(), chunkToBlock(x), 0, chunkToBlock(z)).getChunk();
    }

    //----------------------------------------------//
    // Misc Geometry
    //----------------------------------------------//

    @Override
    public String toString() {
        return "[" + this.getWorldName() + "," + this.getCoordString() + "]";
    }

    public FLocation getRelative(int dx, int dz) {
        return new FLocation(this.worldName, this.x + dx, this.z + dz);
    }

    public double getDistanceTo(FLocation that) {
        double dx = that.x - this.x;
        double dz = that.z - this.z;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public double getDistanceSquaredTo(FLocation that) {
        double dx = that.x - this.x;
        double dz = that.z - this.z;
        return dx * dx + dz * dz;
    }

    public boolean isInChunk(Location loc) {
        if (loc == null) {
            return false;
        }
        Chunk chunk = loc.getChunk();
        return loc.getWorld().getName().equals(getWorldName()) && chunk.getX() == x && chunk.getZ() == z;
    }

    /**
     * Checks if the chunk represented by this FLocation is outside the world border
     *
     * @return whether this location is outside of the border
     */
    public boolean isOutsideWorldBorder() {
        if (!worldBorderSupport) {
            return false;
        }

        WorldBorder worldBorder = getWorld().getWorldBorder();
        double size = worldBorder.getSize() / 2.0;
        double centerX = worldBorder.getCenter().getX();
        double centerZ = worldBorder.getCenter().getZ();
        final int blockX = FLocation.chunkToBlock((int) getX());
        final int blockZ = FLocation.chunkToBlock((int) getZ());
        return centerX - size > blockX ||
                centerX + size <= blockX ||
                centerZ - size > blockZ ||
                centerZ + size <= blockZ;

/*
        WorldBorder border = getWorld().getWorldBorder();
        Chunk chunk = border.getCenter().getChunk();

        int lim = chunkToRegion((int) border.getSize()) - buffer;
        int diffX = Math.abs(chunk.getX() - x);
        int diffZ = Math.abs(chunk.getZ() - z);
        return diffX > lim || diffZ > lim;
  */
    }


    //----------------------------------------------//
    // Some Geometry
    //----------------------------------------------//
    public Set<FLocation> getCircle(double radius) {
        double radiusSquared = radius * radius;

        Set<FLocation> ret = new LinkedHashSet<>();
        if (radius <= 0) {
            return ret;
        }

        int xfrom = (int) Math.floor(this.x - radius);
        int xto = (int) Math.ceil(this.x + radius);
        int zfrom = (int) Math.floor(this.z - radius);
        int zto = (int) Math.ceil(this.z + radius);

        for (int x = xfrom; x <= xto; x++) {
            for (int z = zfrom; z <= zto; z++) {
                FLocation potential = new FLocation(this.worldName, x, z);
                if (this.getDistanceSquaredTo(potential) <= radiusSquared) {
                    ret.add(potential);
                }
            }
        }

        return ret;
    }

    private void recalcHash() {
        this.hash = (this.x << 9) + this.z + (this.worldName != null ? this.worldName.hashCode() : 0);
    }

    //----------------------------------------------//
    // Comparison
    //----------------------------------------------//

    @Override
    public int hashCode() {
        if (hash == 0) {
            recalcHash();
        }
        return hash = (this.x << 9) + this.z + (this.worldName != null ? this.worldName.hashCode() : 0);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FLocation)) {
            return false;
        }

        FLocation that = (FLocation) obj;
        return hashCode() == that.hashCode();
    }
}