package com.gameservergroup.gsgprinter.objs;

import com.gameservergroup.gsgcore.relocations.fastutil.longs.LongOpenHashSet;
import com.gameservergroup.gsgcore.relocations.fastutil.longs.LongSet;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Objects;

public class PrintingData {

    private EnumMap<Material, Integer> placedBlocks;
    private LongSet blocks;
    private final Instant startTime;

    public PrintingData() {
        this.startTime = Instant.now();
    }

    public String getTime() {
        return (double) Duration.between(startTime, Instant.now()).getSeconds() / 60.0 + " minutes";
    }

    public EnumMap<Material, Integer> getPlacedBlocks() {
        return placedBlocks == null ? this.placedBlocks = new EnumMap<>(Material.class) : this.placedBlocks;
    }

    private static long getLongKey(Block block) {
        long combined = 0L;
        combined |= block.getY() << 24;
        combined |= block.getX() & 0xFFFFFFFL << 28;
        combined |= block.getZ() & 0xFFFFFFFL;
        return combined;
    }

    public boolean hasPlacedBlocks() {
        return this.placedBlocks != null && this.blocks != null;
    }

    public LongSet getBlocks() {
        return blocks == null ? this.blocks = new LongOpenHashSet() : this.blocks;
    }

    public void addBlock(Block block) {
        getBlocks().add(getLongKey(block));
    }


    public Instant getStartTime() {
        return startTime;
    }

    public boolean hasPlacedBlock(Block block) {
        return hasPlacedBlocks() && getBlocks().contains(getLongKey(block));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrintingData that = (PrintingData) o;

        if (!Objects.equals(placedBlocks, that.placedBlocks)) return false;
        if (!Objects.equals(blocks, that.blocks)) return false;
        return Objects.equals(startTime, that.startTime);
    }

    @Override
    public int hashCode() {
        int result = placedBlocks != null ? placedBlocks.hashCode() : 0;
        result = 31 * result + (blocks != null ? blocks.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PrintingData{" +
                "placedBlocks=" + placedBlocks +
                ", blocks=" + blocks +
                ", startTime=" + startTime +
                '}';
    }
}
