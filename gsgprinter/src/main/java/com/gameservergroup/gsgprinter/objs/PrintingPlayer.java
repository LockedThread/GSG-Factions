package com.gameservergroup.gsgprinter.objs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.Objects;
import java.util.UUID;

public class PrintingPlayer {

    private final UUID uuid;
    private final EnumMap<Material, Integer> placedBlocks;
    private final Instant startTime;

    public PrintingPlayer(UUID uuid, EnumMap<Material, Integer> placedBlocks) {
        this.uuid = uuid;
        this.placedBlocks = placedBlocks;
        this.startTime = Instant.now();
    }

    public String getTime() {
        return ((double) (Duration.between(startTime, Instant.now()).getSeconds() / 60)) + " minutes";
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

    public Instant getStartTime() {
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

        return startTime.equals(that.startTime) && Objects.equals(uuid, that.uuid);

    }
}
