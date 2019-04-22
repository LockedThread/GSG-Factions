package com.gameservergroup.gsgprinter.objs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Objects;
import java.util.UUID;

public class PrintingPlayer {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    private final UUID uuid;
    private final EnumMap<Material, Integer> placedBlocks;
    private final long startTime;

    public PrintingPlayer(UUID uuid, EnumMap<Material, Integer> placedBlocks) {
        this.uuid = uuid;
        this.placedBlocks = placedBlocks;
        this.startTime = System.currentTimeMillis();
    }

    public String getTime() {
        return SIMPLE_DATE_FORMAT.format(new Date(System.currentTimeMillis() - startTime));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public EnumMap<Material, Integer> getPlacedBlocks() {
        return placedBlocks;
    }

    public long getStartTime() {
        return startTime;
    }

    @Override
    public String toString() {
        return "PrintingPlayer{" +
                "uuid=" + uuid +
                ", placedBlocks=" + placedBlocks +
                ", startTime=" + startTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrintingPlayer that = (PrintingPlayer) o;

        return startTime == that.startTime && Objects.equals(uuid, that.uuid);

    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        return result;
    }
}
