package com.gameservergroup.gsgprinter.objs;

import org.bukkit.Material;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Objects;

public class PrintingData {

    private EnumMap<Material, Integer> placedBlocks;
    private final Instant startTime;

    public PrintingData() {
        this.startTime = Instant.now();
    }

    public String getTime() {
        return ((double) (Duration.between(startTime, Instant.now()).getSeconds() / 60)) + " minutes";
    }

    public EnumMap<Material, Integer> getPlacedBlocks() {
        return placedBlocks == null ? this.placedBlocks = new EnumMap<>(Material.class) : this.placedBlocks;
    }

    public Instant getStartTime() {
        return startTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrintingData that = (PrintingData) o;

        return Objects.equals(placedBlocks, that.placedBlocks) && Objects.equals(startTime, that.startTime);

    }

    @Override
    public int hashCode() {
        int result = placedBlocks != null ? placedBlocks.hashCode() : 0;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PrintingData{" +
                "placedBlocks=" + placedBlocks +
                ", startTime=" + startTime +
                '}';
    }
}
